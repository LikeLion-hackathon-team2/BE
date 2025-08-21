package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "ApiFieldError", description = "필드 단위 오류")
public class ApiFieldError {
    @Schema(description = "오류가 난 필드 경로", example = "items[0].quantity")
    private String field;

    @Schema(description = "오류 설명", example = "최소 1 이상이어야 합니다.")
    private String message;

    public static ApiFieldError of(String field, String message) {
        return new ApiFieldError(field, message);
    }
}
