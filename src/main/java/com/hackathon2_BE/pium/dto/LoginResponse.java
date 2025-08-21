package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "LoginResponse", description = "로그인 응답(토큰 정보)")
public class LoginResponse {
    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwaXVtX3VzZXIxIiwicm9sZSI6IlVTRVIiLCJleHAiOjE3MjUxMjAwMDB9.sig")
    private String accessToken;

    @Schema(description = "만료까지 남은 초", example = "3600")
    private long expiresIn;
}
