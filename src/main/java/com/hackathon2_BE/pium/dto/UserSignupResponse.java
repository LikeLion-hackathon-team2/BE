package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.entity.Shop;
import com.hackathon2_BE.pium.entity.DepositAccount;

import java.time.LocalDateTime;

@Schema(name = "UserSignupResponse", description = "회원가입 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class UserSignupResponse {

    // ===== 기본 인적 =====
    @Schema(description = "사용자 ID", example = "42")
    private Long id;

    @Schema(description = "사용자명(로그인 ID)", example = "seller_0001")
    private String username;

    @Schema(description = "권한/역할", example = "seller")
    private String role;

    @Schema(description = "휴대폰 번호", example = "01022223333")
    private String phoneNumber;

    @Schema(description = "사업자 등록번호(10자리)", example = "1234567890")
    private String businessNumber;

    @Schema(description = "가게명", example = "멋사네 가게")
    private String shopName;

    @Schema(description = "입금 계좌 정보")
    private DepositAccountDto depositAccount;

    @Schema(description = "생성 시각 (ISO-8601 문자열)", example = "2025-08-24T02:55:00")
    private String createdAt;

    public static UserSignupResponse of(User user) {
        UserSignupResponse r = new UserSignupResponse();
        if (user == null) return r;

        r.id = user.getId();
        r.username = user.getUsername();
        r.role = String.valueOf(user.getRole());
        r.phoneNumber = user.getPhoneNumber();
        r.businessNumber = user.getBusinessNumber();
        r.createdAt = formatDate(user.getCreatedAt());

        Shop shop = user.getShop();
        if (shop != null) {
            r.shopName = shop.getName();

            DepositAccount acc = shop.getDepositAccount();
            if (acc != null) {
                r.depositAccount = new DepositAccountDto(
                        acc.getBank(),
                        mask(acc.getNumber()),
                        acc.getHolder()
                );
            }
        }
        return r;
    }

    private static String formatDate(LocalDateTime dt) {
        if (dt == null) return null;
        return dt.toString();
    }

    private static String mask(String num) {
        if (num == null) return null;
        int n = num.length();
        if (n <= 4) return "****";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - 4; i++) sb.append('*');
        sb.append(num.substring(n - 4));
        return sb.toString();
    }

    // ---------- 내부 DTO ----------
    @Schema(name = "DepositAccountDto", description = "가게의 기본 입금 계좌")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositAccountDto {

        @Schema(description = "은행명", example = "국민")
        private String bank;

        @Schema(description = "마스킹된 계좌번호(응답 전용)", example = "***********1111")
        private String number;

        @Schema(description = "예금주명", example = "멋사네")
        private String holder;
    }
}
