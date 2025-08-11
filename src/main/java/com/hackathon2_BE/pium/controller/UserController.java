package com.hackathon2_BE.pium.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon2_BE.pium.dto.UserDTO;
import com.hackathon2_BE.pium.model.ApiResponse;
import com.hackathon2_BE.pium.model.User;
import com.hackathon2_BE.pium.service.InvalidInputException;
import com.hackathon2_BE.pium.service.UserService;
import com.hackathon2_BE.pium.service.UsernameAlreadyExistsException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.signup(userDTO);
            return new ResponseEntity<>(new ApiResponse("회원가입 완료", user), HttpStatus.CREATED);
        } catch (InvalidInputException e) {
            return new ResponseEntity<>(new ApiResponse("요청 필드가 올바르지 않습니다.", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UsernameAlreadyExistsException e) {
            return new ResponseEntity<>(new ApiResponse("이미 사용 중인 아이디입니다.", e.getMessage()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("서버 오류가 발생했습니다.", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}