package com.hackathon2_BE.pium.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DataGoKrBizStatusErrorResponse {
    private Integer code;   // 예: -4
    private String msg;     // 예: "등록되지 않은 인증키 입니다."
}
