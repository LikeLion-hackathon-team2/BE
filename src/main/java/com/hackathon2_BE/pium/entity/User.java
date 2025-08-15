package com.hackathon2_BE.pium.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 아이디: 유니크 + not null
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    // 비밀번호: not null
    @Column(nullable = false, length = 100)
    private String password;

    // "consumer" | "seller"
    @Column(nullable = false, length = 20)
    private String role;

    // 판매자만 사용(consumer는 null)
    @Column(length = 20)
    private String businessNumber;

    // 하이픈 제거된 숫자 10~11자리 저장 권장
    @Column(length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // === JPA 기본 생성자 ===
    public User() { }

    // === 라이프사이클 콜백 ===
    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // === Getter / Setter ===
    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id; }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) { this.password = password; }

    public String getRole() {
        return role;
    }
    public void setRole(String role) { this.role = role; }

    public String getBusinessNumber() {
        return businessNumber;
    }
    public void setBusinessNumber(String businessNumber) { this.businessNumber = businessNumber; }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
