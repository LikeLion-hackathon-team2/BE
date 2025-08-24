package com.hackathon2_BE.pium.external;

import com.hackathon2_BE.pium.dto.DataGoKrBizStatusRequest;
import com.hackathon2_BE.pium.dto.DataGoKrBizStatusResponse;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DataGoKrClient {

    private final RestTemplate restTemplate;

    @Value("${external.datagokr.base-url}")
    private String baseUrl;

    // 반드시 Decoding 키(=원문 Base64, 퍼센트 인코딩 X)
    @Value("${external.datagokr.service-key-dec}")
    private String serviceKeyDec;

    private String endpoint() {
        // https://api.odcloud.kr/api/nts-businessman/v1/status
        return baseUrl + "/api/nts-businessman/v1/status";
    }

    /**
     * 사업자번호 1건 상태 조회(헤더 인증)
     */
    public DataGoKrBizStatusResponse callStatus(String businessNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Infuser " + serviceKeyDec);

        var body = DataGoKrBizStatusRequest.builder()
                .bNo(List.of(businessNumber))
                .build();

        var entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<DataGoKrBizStatusResponse> resp = restTemplate.exchange(
                    URI.create(endpoint()),
                    HttpMethod.POST,
                    entity,
                    DataGoKrBizStatusResponse.class
            );

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new InvalidInputException("외부 API 통신 오류: " + resp.getStatusCode());
            }

            var bodyObj = resp.getBody();

            if ("ERROR".equalsIgnoreCase(bodyObj.getStatus())) {
                String em = (bodyObj.getError() != null) ? bodyObj.getError().getMsg() : "알 수 없는 오류";
                throw new InvalidInputException("외부 API 오류: " + em);
            }

            return bodyObj;
        } catch (RestClientException e) {
            throw new InvalidInputException("외부 API 통신 오류: " + e.getMessage());
        }
    }

    /**
     * “정상(계속) 사업자”가 아니면 InvalidInputException 발생.
     * 회원가입 ‘판매자’ 케이스에서 호출해서 막으면 됨.
     */
    public void validateActiveOrThrow(String businessNumber) {
        var res = callStatus(businessNumber);

        if (res.getMatchCnt() == null || res.getMatchCnt() < 1 || res.getData() == null || res.getData().isEmpty()) {
            throw new InvalidInputException("국세청에 등록되지 않은 사업자등록번호입니다.");
        }

        var row = res.getData().get(0);

        // b_no가 일치하는지 방어적으로 확인
        if (!Objects.equals(row.getBNo(), businessNumber)) {
            throw new InvalidInputException("조회 응답의 사업자번호가 일치하지 않습니다.");
        }

        // 휴/폐업 판정(보수적으로 end_dt 존재 또는 코드/문구로 체크)
        boolean closedByText = row.getBStt() != null && row.getBStt().contains("폐업");
        boolean closedByCode = "03".equals(row.getBSttCd());
        boolean hasEndDt = row.getEndDt() != null && !row.getEndDt().isBlank();

        if (closedByText || closedByCode || hasEndDt) {
            throw new InvalidInputException("폐업(또는 유효하지 않은) 사업자등록번호입니다.");
        }
    }
}
