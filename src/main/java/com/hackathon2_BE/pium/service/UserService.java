package com.hackathon2_BE.pium.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hackathon2_BE.pium.dto.MeResponse;
import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User signup(UserDTO userDTO) {
        // 1) 입력값 검증
        if (!isValidUsername(userDTO.getUsername())) {
            throw new InvalidInputException("아이디는 4~20자의 영문 소문자, 숫자, 밑줄만 허용됩니다.");
        }
        if (!isValidPassword(userDTO.getPassword())) {
            throw new InvalidInputException("비밀번호는 8~64자여야 하며, 영문과 숫자를 포함해야 합니다.");
        }
        if (!isValidRole(userDTO.getRole())) {
            throw new InvalidInputException("role은 'consumer' 또는 'seller'만 허용됩니다.");
        }

        // 전화번호 정규화/검증
        String rawPhone = userDTO.getPhoneNumber();
        String normalizedPhone = normalizePhone(rawPhone);
        if (!isValidPhone(normalizedPhone)) {
            throw new InvalidInputException("전화번호는 하이픈(-) 없이 숫자 10~11자리여야 합니다.");
        }

        // 판매자일 때 사업자번호 정규화/검증
        String normalizedBusinessNumber = null;
        if ("seller".equals(userDTO.getRole())) {
            String rawBusinessNumber = userDTO.getBusinessNumber();
            normalizedBusinessNumber = normalizeBusinessNumber(rawBusinessNumber);
            if (normalizedBusinessNumber == null || normalizedBusinessNumber.isEmpty()) {
                throw new InvalidInputException("판매자 가입 시 사업자 번호는 필수입니다.");
            }
            if (!isValidBusinessNumber(normalizedBusinessNumber)) {
                throw new InvalidInputException("유효한 사업자 번호를 입력하세요.");
            }
        }

        // 2) 엔티티 생성 및 저장 (비밀번호 해싱)
        User user = new User();
        user.setUsername(userDTO.getUsername());

        String encoded = passwordEncoder.encode(userDTO.getPassword()); // 해싱
        user.setPassword(encoded);

        user.setRole(userDTO.getRole());
        user.setPhoneNumber(normalizedPhone); // 🔹 정규화된 값 저장
        user.setCreatedAt(LocalDateTime.now());

        if ("seller".equals(userDTO.getRole())) {
            user.setBusinessNumber(normalizedBusinessNumber); // 🔹 정규화된 값 저장
        }

        return userRepository.save(user);
    }

    public MeResponse getMeByUsername(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return MeResponse.from(u);
    }

    // ======= 검증/정규화 유틸 =======

    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-z0-9_]{4,20}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{8,64}$");
    }

    private boolean isValidRole(String role) {
        return "consumer".equals(role) || "seller".equals(role);
    }

    private String normalizePhone(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", ""); // 숫자만
    }

    private boolean isValidPhone(String digitsOnly) {
        return digitsOnly != null && digitsOnly.matches("^\\d{10,11}$");
    }

    private String normalizeBusinessNumber(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", ""); // 숫자만 (하이픈 제거)
    }

    private boolean isValidBusinessNumber(String businessNumber) {
        return businessNumber != null && businessNumber.matches("^\\d{10}$"); // 10자리
    }
}
