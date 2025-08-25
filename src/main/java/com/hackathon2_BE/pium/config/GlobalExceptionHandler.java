package com.hackathon2_BE.pium.config;

import com.hackathon2_BE.pium.dto.ApiErrorResponse;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.RateLimitException;
import com.hackathon2_BE.pium.exception.ResourceNotFoundException;
import com.hackathon2_BE.pium.exception.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalid(InvalidInputException ex, HttpServletRequest req) {
        log.warn("Invalid input: {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of("INVALID_INPUT", ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        log.warn("Unauthorized: {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of("UNAUTHORIZED", "인증이 필요합니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        log.warn("Forbidden: {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of("FORBIDDEN", "권한이 없습니다."));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("Not found: {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, HttpServletRequest req) {
        log.warn("Username conflict: {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of("CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnique(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Data integrity violation: {} {} - {}", req.getMethod(), req.getRequestURI(),
                ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of("CONFLICT", "중복된 값이 존재합니다."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        log.warn("Method not supported: {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiErrorResponse.of("METHOD_NOT_ALLOWED", "지원하지 않는 요청입니다."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        log.warn("Type mismatch: {} {} - param='{}' value='{}'", req.getMethod(), req.getRequestURI(),
                ex.getName(), ex.getValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of("TYPE_MISMATCH", "파라미터 타입이 올바르지 않습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleServerError(Exception ex, HttpServletRequest req) {
        log.error("Unhandled server error on {} {} ", req.getMethod(), req.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of("SERVER_ERROR", "서버 오류가 발생했습니다."));
    }
}
