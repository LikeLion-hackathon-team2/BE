package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(name = "ApiErrorResponse", description = "표준 오류 응답")
public class ApiErrorResponse {
    @Schema(description = "성공 여부(항상 false)", example = "false")
    private boolean success;

    @Schema(description = "에러 코드", example = "INVALID_INPUT")
    private String code;

    @Schema(description = "에러 메시지", example = "입력값이 올바르지 않습니다.")
    private String message;

    @Schema(description = "상세 오류 목록", example = "[\"items[0].quantity: 최소 1\", \"price: 0 이상\"]")
    private List<String> errors;

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(false, code, message, List.of());
    }
}
