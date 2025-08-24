package com.hackathon2_BE.pium.external;

import com.hackathon2_BE.pium.dto.DataGoKrBizStatusRequest;
import com.hackathon2_BE.pium.dto.DataGoKrBizStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BusinessNumberVerifier {

    private final RestTemplate restTemplate;

    @Value("${external.datagokr.base-url}")
    private String baseUrl;

    // 반드시 'Decoding' 키(퍼센트 인코딩 X)
    @Value("${external.datagokr.service-key-dec}")
    private String serviceKeyDec;

    public static final class Result {
        public final boolean valid;
        public final String reason;    // 실패 사유(통신/휴·폐업/불일치 등)
        public Result(boolean valid, String reason) {
            this.valid = valid;
            this.reason = reason;
        }
        public static Result ok() { return new Result(true, "OK"); }
        public static Result fail(String msg) { return new Result(false, msg); }
    }

    private String endpoint() {
        // https://api.odcloud.kr/api/nts-businessman/v1/status
        return baseUrl + "/api/nts-businessman/v1/status";
    }

    /**
     * 사업자번호 10자리 검증 (헤더 인증)
     * - 계속사업자(폐업/휴업 아님)일 때만 valid=true
     */
    public Result verify10Digits(String bno) {
        if (bno == null || !bno.matches("^\\d{10}$")) {
            return Result.fail("사업자 번호는 숫자 10자리여야 합니다.");
        }

        try {
            DataGoKrBizStatusResponse body = callStatus(bno);

            if (body.getMatchCnt() == null || body.getMatchCnt() < 1 || body.getData() == null || body.getData().isEmpty()) {
                return Result.fail("국세청에 등록되지 않은 사업자등록번호입니다.");
            }

            var row = body.getData().get(0);

            // 방어적으로 동일 번호인지 체크
            if (!Objects.equals(row.getBNo(), bno)) {
                return Result.fail("조회 응답의 사업자번호가 일치하지 않습니다.");
            }

            // 휴/폐업 판정: 코드/문구/폐업일
            boolean closedByCode = "03".equals(row.getBSttCd());          // 03=폐업
            boolean closedByText = row.getBStt() != null && row.getBStt().contains("폐업");
            boolean hasEndDt     = row.getEndDt() != null && !row.getEndDt().isBlank();

            if (closedByCode || closedByText || hasEndDt) {
                return Result.fail("폐업(또는 유효하지 않은) 사업자등록번호입니다.");
            }

            // 통과
            return Result.ok();

        } catch (HttpStatusCodeException e) {
            // data.go.kr 오류 응답(예: {"code":-4,"msg":"등록되지 않은 인증키 입니다."})
            String body = e.getResponseBodyAsString();
            return Result.fail("외부 API 통신 오류: " + e.getStatusCode() + " " + body);
        } catch (RestClientException e) {
            return Result.fail("외부 API 통신 오류: " + e.getMessage());
        }
    }

    private DataGoKrBizStatusResponse callStatus(String bno) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Infuser " + serviceKeyDec);

        var reqBody = DataGoKrBizStatusRequest.builder()
                .bNo(List.of(bno))
                .build();

        var entity = new HttpEntity<>(reqBody, headers);

        ResponseEntity<DataGoKrBizStatusResponse> resp = restTemplate.exchange(
                URI.create(endpoint()),
                HttpMethod.POST,
                entity,
                DataGoKrBizStatusResponse.class
        );

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RestClientException("unexpected status: " + resp.getStatusCode());
        }
        return resp.getBody();
    }
}
