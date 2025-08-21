package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Schema(name = "LoginRequest", description = "로그인 요청")
public class LoginRequest {
    @Schema(description = "사용자 아이디", example = "pium_user1")
    private String username;

    @Schema(description = "비밀번호", example = "pium1234!")
    private String password;
}
