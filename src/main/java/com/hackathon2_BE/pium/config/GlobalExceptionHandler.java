package com.hackathon2_BE.pium.config;

import com.hackathon2_BE.pium.dto.ApiErrorResponse;
import com.hackathon2_BE.pium.exception.RateLimitException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 - 리소스를 찾을 수 없는 경우
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of("NOT_FOUND", ex.getMessage()));
    }

    // 429 - 요청 제한
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimit(RateLimitException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiErrorResponse.of("RATE_LIMITED", ex.getMessage()));
    }

    // 500 - 서버 내부 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of("SERVER_ERROR", "서버 오류가 발생했습니다."));
    }
}
