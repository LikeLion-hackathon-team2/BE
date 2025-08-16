package com.hackathon2_BE.pium.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon2_BE.pium.entity.User;

public class MeResponse {
    private Long id;
    private String username;
    private String role;
    private String phoneNumber;

    @JsonProperty("business_number")
    private String businessNumber;

    @JsonProperty("created_at")
    private String createdAt;

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
        // User 엔티티에 updatedAt이 없으면 createdAt으로 대체
        m.updatedAt = toIsoUtc(u.getCreatedAt());
        return m;
    }

    private static String toIsoUtc(LocalDateTime time) {
        if (time == null) return null;
        // LocalDateTime → UTC 기준 ISO-8601 문자열
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
