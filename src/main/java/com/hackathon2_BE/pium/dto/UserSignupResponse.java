package com.hackathon2_BE.pium.dto;

import com.hackathon2_BE.pium.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UserSignupResponse", description = "회원가입 결과")
public class UserSignupResponse {
    @Schema(description = "사용자 ID", example = "42")
    private Long id;

    @Schema(description = "아이디", example = "pium_user1")
    private String username;

    @Schema(description = "역할", example = "USER")
    private String role;

    @Schema(description = "휴대폰 번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "사업자번호(SELLER만)", example = "123-45-67890")
    private String businessNumber;

    @Schema(description = "생성일시", example = "2025-08-22T12:34:56")
    private String createdAt;

    public static UserSignupResponse from(User u) {
        return UserSignupResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .role(u.getRole().name())
                .phoneNumber(u.getPhoneNumber())
                .businessNumber(u.getBusinessNumber())
                .createdAt(u.getCreatedAt() == null ? null : u.getCreatedAt().toString())
                .build();
    }
}
