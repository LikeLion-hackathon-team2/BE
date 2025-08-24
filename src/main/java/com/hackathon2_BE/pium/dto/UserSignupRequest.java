package com.hackathon2_BE.pium.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Schema(name = "UserSignupRequest", description = "회원가입 요청")
public class UserSignupRequest {

    @Schema(description = "사용자 아이디", example = "pium_user1")
    private String username;

    @Schema(description = "비밀번호", example = "pium1234!")
    private String password;

    @Schema(description = "역할(consumer 또는 seller)", example = "seller")
    private String role;

    @Schema(description = "휴대폰 번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "사업자번호(seller만)", example = "1234567890")
    private String businessNumber; // seller만

    private String shopName;

    private DepositAccountDto depositAccount;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    @Schema(name="DepositAccountDto")
    public static class DepositAccountDto {
        private String bank;
        private String number;
        private String holder;
    }
}
