package com.hackathon2_BE.pium.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity  // JPA 엔티티로 지정
public class User {

    @Id  // 기본 키로 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 전략 설정
    private Long id;
    private String username;
    private String password;
    private String role;
    private String businessNumber;
    private String phoneNumber;
    private LocalDateTime createdAt;

    // 생성자
    public User(Long id, String username, String password, String role, String phoneNumber, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.businessNumber = businessNumber;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    // 기본 생성자 (JPA에서 필요)
    public User() {}

    // Getter와 Setter 메소드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}