package com.hackathon2_BE.pium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.MeResponse;
import com.hackathon2_BE.pium.service.UserService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1-1) 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody UserDTO userDTO) {

        User user = userService.signup(userDTO);
        
        ApiResponse<User> response = new ApiResponse<>(
                true,                           
                "CREATED",                       
                "회원가입 완료",                    
                user                             
        );
        return ResponseEntity.status(201).body(response);  
    }

    // 1-3) 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            // GlobalExceptionHandler에서 401로 매핑
            throw new org.springframework.security.core.AuthenticationException("UNAUTHORIZED") {};
        }
        String username = authentication.getName(); // 토큰에서 올라온 subject/username
        MeResponse body = userService.getMeByUsername(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "내 정보 조회 성공", body));
    }
}
