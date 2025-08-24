package com.hackathon2_BE.pium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.MeResponse;
import com.hackathon2_BE.pium.dto.UserSignupRequest;
import com.hackathon2_BE.pium.dto.UserSignupResponse;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원가입",
            description = "새 사용자 회원가입",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "일반 사용자",
                                            value = """
                        {
                          "username": "user_0001",
                          "password": "Abcd1234!",
                          "role": "consumer",
                          "phoneNumber": "01012345678"
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "판매자",
                                            value = """
                        {
                          "username": "user_0002",
                          "password": "Abcd1234",
                          "role": "seller",
                          "phoneNumber": "01055557777",
                          "businessNumber": "1234567890",
                          "shopName": "멋사네 가게",
                          "depositAccount": { "bank": "국민", "number": "11111111111111", "holder": "멋사네" }
                        }
                        """
                                    )
                            }
                    )
            )
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody UserSignupRequest userDTO) {
        User user = userService.signup(userDTO);
        UserSignupResponse data = UserSignupResponse.of(user);
        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, "CREATED", "회원가입 완료", data));
    }

    @Operation(summary = "내 정보 조회", description = "인증된 사용자의 프로필 반환")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me() {
        MeResponse me = userService.getMe();
        return ResponseEntity.status(200)
                .body(new ApiResponse<>(true, "OK", "내 정보 조회 성공", me));
    }
}
