package com.hackathon2_BE.pium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiFieldError {
    private String field;
    private String message;

    public static ApiFieldError of(String field, String message) {
        return new ApiFieldError(field, message);
    }
}
