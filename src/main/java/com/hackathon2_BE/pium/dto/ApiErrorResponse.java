package com.hackathon2_BE.pium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String code;
    private String message;
    private List<String> errors;

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(false, code, message, List.of());
    }
}
