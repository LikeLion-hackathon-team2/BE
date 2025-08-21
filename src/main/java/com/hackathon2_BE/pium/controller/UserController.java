package com.hackathon2_BE.pium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.MeResponse;
import com.hackathon2_BE.pium.dto.UserDTO;
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
                          "username": "pium_user1",
                          "password": "p@ssw0rd!",
                          "role": "USER",
                          "phoneNumber": "010-1234-5678"
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "판매자",
                                            value = """
                        {
                          "username": "seller_ace",
                          "password": "S3ll3r!234",
                          "role": "SELLER",
                          "phoneNumber": "010-5555-7777",
                          "businessNumber": "123-45-67890"
                        }
                        """
                                    )
                            }
                    )
            )
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody UserDTO userDTO) {
        User user = userService.signup(userDTO);
        UserSignupResponse data = UserSignupResponse.from(user);
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
