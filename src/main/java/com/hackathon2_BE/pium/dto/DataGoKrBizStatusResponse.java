package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DataGoKrBizStatusResponse {

    // 예: "OK" 또는 "ERROR"
    private String status;

    @JsonProperty("request_cnt")
    private Integer requestCnt;

    @JsonProperty("match_cnt")
    private Integer matchCnt;

    // 오류일 경우 내려올 수 있음(엔드포인트/상황에 따라 구조가 조금 다르니 방어적으로 둠)
    private DataGoKrBizStatusErrorResponse error;

    private List<Row> data;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        @JsonProperty("b_no")
        private String bNo;

        @JsonProperty("b_stt")
        private String bStt;       // 예: "계속사업자", "폐업자" 등

        @JsonProperty("b_stt_cd")
        private String bSttCd;     // 예: "01"(계속), "02"(휴업), "03"(폐업) 등

        @JsonProperty("tax_type")
        private String taxType;    // 예: "부가가치세 일반과세자"

        @JsonProperty("tax_type_cd")
        private String taxTypeCd;

        @JsonProperty("end_dt")
        private String endDt;      // 폐업일(YYYYMMDD) 또는 빈값
    }
}
