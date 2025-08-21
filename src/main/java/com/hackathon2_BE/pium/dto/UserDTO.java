package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Schema(name = "UserSignupRequest", description = "회원가입 요청")
public class UserDTO {
    @Schema(description = "사용자 아이디", example = "pium_user1")
    private String username;

    @Schema(description = "비밀번호", example = "pium1234!")
    private String password;

    @Schema(description = "역할(USER 또는 SELLER)", example = "USER")
    private String role;

    @Schema(description = "휴대폰 번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "사업자번호(SELLER만)", example = "123-45-67890")
    private String businessNumber; // seller만
}
