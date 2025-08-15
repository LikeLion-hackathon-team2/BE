package com.hackathon2_BE.pium.dto;

import com.hackathon2_BE.pium.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 직렬화/테스트 편의용
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSignupResponse {
    private Long id;
    private String username;
    private String role;
    private String phoneNumber;
    private String businessNumber;
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
