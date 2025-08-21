package com.hackathon2_BE.pium.controller;

import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.dto.LoginRequest;
import com.hackathon2_BE.pium.dto.LoginResponse;
import com.hackathon2_BE.pium.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증/로그인 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @Operation(
            summary = "로그인",
            description = "username/password로 로그인하여 JWT를 발급받습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(name = "login-ok", value = """
                            {
                              "success": true,
                              "code": "OK",
                              "message": "로그인 성공",
                              "data": {
                                "tokenType": "Bearer",
                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                "expiresIn": 3600
                              }
                            }
                            """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "login-req", value = """
                    {
                      "username": "alice",
                      "password": "pass1234"
                    }
                    """))
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest req) {
        LoginResponse data = authService.login(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "로그인 성공", data));
    }
}
