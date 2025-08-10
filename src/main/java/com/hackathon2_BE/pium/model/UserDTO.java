package com.hackathon2_BE.pium.model;

public class UserDTO {
    private String username;
    private String password;
    private String role;

    // 기본 생성자 (Jackson 역직렬화용)
    public UserDTO() { }

    // 전체 필드 생성자
    public UserDTO(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
