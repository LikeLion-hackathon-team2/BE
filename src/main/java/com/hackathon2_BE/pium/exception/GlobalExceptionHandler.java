package com.hackathon2_BE.pium.exception;

import com.hackathon2_BE.pium.service.InvalidInputException;
import com.hackathon2_BE.pium.service.UsernameAlreadyExistsException;
import com.hackathon2_BE.pium.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // InvalidInputException 처리
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleInvalidInputException(InvalidInputException ex) {
        ApiResponse response = new ApiResponse("요청 필드가 올바르지 않습니다.", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // UsernameAlreadyExistsException 처리
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        ApiResponse response = new ApiResponse("이미 사용 중인 아이디입니다.", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 모든 예외를 처리하는 기본 처리기
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        ApiResponse response = new ApiResponse("서버 오류가 발생했습니다.", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
