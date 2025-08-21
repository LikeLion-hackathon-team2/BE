package com.hackathon2_BE.pium.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon2_BE.pium.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MeResponse", description = "내 정보 응답")
public class MeResponse {

    @Schema(description = "사용자 ID", example = "6")
    private Long id;

    @Schema(description = "사용자명(로그인 아이디)", example = "apple_farm")
    private String username;

    @Schema(description = "역할(소문자)", example = "buyer")
    private String role;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "사업자등록번호(판매자만)", example = "123-45-67890")
    @JsonProperty("business_number")
    private String businessNumber;

    @Schema(description = "생성 시각(ISO-8601 UTC)", example = "2025-08-22T00:00:00Z")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "수정 시각(ISO-8601 UTC)", example = "2025-08-22T12:34:56Z")
    @JsonProperty("updated_at")
    private String updatedAt;

    public static MeResponse from(User u) {
        MeResponse m = new MeResponse();
        m.id = u.getId();
        m.username = u.getUsername();
        m.role = (u.getRole() != null) ? u.getRole().name().toLowerCase() : null;
        m.phoneNumber = u.getPhoneNumber();
        m.businessNumber = u.getBusinessNumber();
        m.createdAt = toIsoUtc(u.getCreatedAt());
        m.updatedAt = toIsoUtc(u.getCreatedAt()); // updatedAt 필드가 없으면 createdAt 재사용
        return m;
    }

    private static String toIsoUtc(LocalDateTime time) {
        if (time == null) return null;
        return time.atOffset(ZoneOffset.UTC).toInstant().toString();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBusinessNumber() { return businessNumber; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
