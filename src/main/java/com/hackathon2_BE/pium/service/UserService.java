package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.MeResponse;
import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.exception.InvalidInputException;
import com.hackathon2_BE.pium.exception.UnauthenticatedException;
import com.hackathon2_BE.pium.exception.UsernameAlreadyExistsException;
import com.hackathon2_BE.pium.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ===================== 회원가입 =====================
    @Transactional
    public User signup(UserDTO userDTO) {

        if (!isValidUsername(userDTO.getUsername()))
            throw new InvalidInputException("아이디는 4~20자의 영문 소문자, 숫자, 밑줄만 허용됩니다.");
        if (!isValidPassword(userDTO.getPassword()))
            throw new InvalidInputException("비밀번호는 8~64자이며 영문과 숫자를 포함해야 합니다.");
        if (!isValidRole(userDTO.getRole()))
            throw new InvalidInputException("role은 'consumer' 또는 'seller'만 허용됩니다.");

        if (userRepository.existsByUsername(userDTO.getUsername()))
            throw new UsernameAlreadyExistsException("이미 사용 중인 아이디입니다.");

        String normalizedPhone = normalizePhone(userDTO.getPhoneNumber());
        if (!isValidPhone(normalizedPhone))
            throw new InvalidInputException("전화번호는 숫자 10~11자리여야 합니다.");

        boolean isSeller = "seller".equalsIgnoreCase(userDTO.getRole());
        String normalizedBusinessNumber = null;
        if (isSeller) {
            normalizedBusinessNumber = normalizeBusinessNumber(userDTO.getBusinessNumber());
            if (!isValidBusinessNumber(normalizedBusinessNumber))
                throw new InvalidInputException("사업자 번호는 숫자 10자리여야 합니다.");
            if (userRepository.existsByBusinessNumber(normalizedBusinessNumber))
                throw new InvalidInputException("이미 등록된 사업자 번호입니다.");
        }

        User.Role roleEnum = isSeller ? User.Role.SELLER : User.Role.CONSUMER;
        String hashedPw = passwordEncoder.encode(userDTO.getPassword());

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(hashedPw)
                .role(roleEnum)
                .phoneNumber(normalizedPhone)
                .businessNumber(isSeller ? normalizedBusinessNumber : null)
                .build();

        return userRepository.save(user);
    }

    // ===================== 내 정보 조회 =====================
    @Transactional(readOnly = true)
    public MeResponse getMe() {
        String username = getCurrentUsernameOrThrow();  // SecurityContext에서 로그인 유저 추출
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return MeResponse.from(user);                   // 명세서 포맷으로 변환
    }

    private String getCurrentUsernameOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            // GlobalExceptionHandler에서 401 UNAUTHORIZED로 매핑
            throw new UnauthenticatedException("유효하지 않거나 만료된 토큰입니다.");
        }
        return auth.getName();
    }

    // ===================== 유효성/정규화 =====================
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-z0-9_]{4,20}$");
    }
    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{8,64}$");
    }
    private boolean isValidRole(String role) {
        return "consumer".equalsIgnoreCase(role) || "seller".equalsIgnoreCase(role);
    }
    private String normalizePhone(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", "");
    }
    private boolean isValidPhone(String digitsOnly) {
        return digitsOnly != null && digitsOnly.matches("^\\d{10,11}$");
    }
    private String normalizeBusinessNumber(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\D", "");
    }
    private boolean isValidBusinessNumber(String businessNumber) {
        return businessNumber != null && businessNumber.matches("^\\d{10}$");
    }
}
