package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.LoginRequest;
import com.hackathon2_BE.pium.dto.LoginResponse;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.repository.UserRepository;
import com.hackathon2_BE.pium.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT에는 대문자(ENUM 원형), 응답 JSON에는 소문자 권장
        String roleUpper = user.getRole().name();
        String roleLower = roleUpper.toLowerCase();

        String token = tokenProvider.createToken(user.getUsername(), roleUpper);

        return LoginResponse.builder()
                .tokenType("Bearer")
                .accessToken(token)
                .expiresIn(tokenProvider.getExpiresInSeconds())
                .role(roleLower)
                .build();
    }
}
