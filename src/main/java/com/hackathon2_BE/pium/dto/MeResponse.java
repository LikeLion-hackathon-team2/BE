package com.hackathon2_BE.pium.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon2_BE.pium.entity.User;
import java.time.LocalDateTime;

public class MeResponse {
    private Long id;
    private String username;
    private String role;
    private String phoneNumber;

    @JsonProperty("business_number")
    private String businessNumber;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static MeResponse from(User u) {
        MeResponse m = new MeResponse();
        m.id = u.getId();
        m.username = u.getUsername();
        m.role = u.getRole();
        m.phoneNumber = u.getPhoneNumber();
        m.businessNumber = u.getBusinessNumber();
        m.createdAt = u.getCreatedAt();
        // updatedAt이 엔티티에 없다면 createdAt으로 대체하거나 엔티티에 필드 추가
        m.updatedAt = (u.getUpdatedAt() != null) ? u.getUpdatedAt() : u.getCreatedAt();
        return m;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBusinessNumber() { return businessNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
