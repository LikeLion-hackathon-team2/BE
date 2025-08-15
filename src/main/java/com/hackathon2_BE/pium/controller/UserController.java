package com.hackathon2_BE.pium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.MeResponse;
import com.hackathon2_BE.pium.dto.UserSignupResponse; // ⬅ 추가
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody UserDTO userDTO) {
        User user = userService.signup(userDTO);
        UserSignupResponse data = UserSignupResponse.from(user); // ⬅ 변환 편의 메서드 사용
        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, "CREATED", "회원가입 완료", data));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        MeResponse me = userService.getMe();  // 내부에서 현재 사용자 추출
        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, "CREATED", "내 정보 조회 성공", me));
    }
}
