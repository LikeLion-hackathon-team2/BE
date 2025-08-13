package com.hackathon2_BE.pium.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.entity.User;
import com.hackathon2_BE.pium.dto.ApiResponse;
import com.hackathon2_BE.pium.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1-1) 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody UserDTO userDTO) {

        User user = userService.signup(userDTO);
        
        ApiResponse<User> response = new ApiResponse<>(
                true,                           
                "CREATED",                       
                "회원가입 완료",                    
                user                             
        );
        return ResponseEntity.status(201).body(response);  
    }
}
