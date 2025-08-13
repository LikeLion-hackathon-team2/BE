package com.hackathon2_BE.pium.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User signup(UserDTO userDTO) {
        // 유효성 검증
        if (!isValidUsername(userDTO.getUsername())) {
            throw new InvalidInputException("아이디는 4~20자의 영문 소문자, 숫자, 밑줄만 허용됩니다.");
        }
        if (!isValidPassword(userDTO.getPassword())) {
            throw new InvalidInputException("비밀번호는 8~64자여야 하며, 영문과 숫자를 포함해야 합니다.");
        }
        if (!isValidRole(userDTO.getRole())) {
            throw new InvalidInputException("role은 'consumer' 또는 'seller'만 허용됩니다.");
        }
        String rawPhone = userDTO.getPhoneNumber();
        String normalizedPhone = normalizePhone(rawPhone); // 숫자만 추출
        if (!isValidPhone(normalizedPhone)) {
            throw new InvalidInputException("전화번호는 하이픈(-) 없이 숫자 10~11자리여야 합니다.");
        }

        // 실제 회원가입 처리 로직 (예: 데이터베이스에 저장)
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setCreatedAt(LocalDateTime.now());  // 회원가입 시점

        // 데이터베이스에 저장 (ID는 자동 생성됨)
        return userRepository.save(user);
    }

    // 유효한 아이디인지 체크
    private boolean isValidUsername(String username) {
        // 아이디는 4~20자, 영문 소문자, 숫자, 밑줄만 허용
        return username != null && username.matches("^[a-z0-9_]{4,20}$");
    }

    // 유효한 비밀번호인지 체크
    private boolean isValidPassword(String password) {
        // 비밀번호는 8~64자, 영문과 숫자를 포함해야 함
        return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{8,64}$");
    }

    // 유효한 역할(role)인지 체크
    private boolean isValidRole(String role) {
        // role은 'consumer' 또는 'seller'만 허용
        return "consumer".equals(role) || "seller".equals(role);
    }

    private String normalizePhone(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", ""); // 숫자만
    }

    private boolean isValidPhone(String digitsOnly) {
        return digitsOnly != null && digitsOnly.matches("^\\d{10,11}$");
    }

}