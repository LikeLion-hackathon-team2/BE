package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "ApiResponse", description = "표준 성공 응답 래퍼")
public class ApiResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "결과 코드", example = "OK")
    private String code;

    @Schema(description = "메시지", example = "요청 성공")
    private String message;

    @Schema(description = "실제 응답 데이터(엔드포인트별로 달라짐)")
    private T data;
}
