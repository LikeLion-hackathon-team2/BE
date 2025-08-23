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


    @Schema(description = "역할(CONSUMER 또는 SELLER)", example = "CONSUMER")
    private String role;


    @Schema(description = "휴대폰 번호(숫자만)", example = "01012345678")
    private String phoneNumber;


    @Schema(description = "사업자번호(SELLER만, 숫자만)", example = "1234567890")
    private String businessNumber;
}
