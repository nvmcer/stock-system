package com.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exception.ApiResponse;
import com.security.JwtUtil;
import com.user.dto.LoginRequestDto;
import com.user.dto.RegisterRequestDto;
import com.user.dto.UserResponseDto;
import com.user.entity.User;
import com.user.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final UserService userService;

    private final JwtUtil jwtUtil = new JwtUtil();

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@RequestBody RegisterRequestDto request) {
        User saved = userService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(
            new UserResponseDto(saved.getId(), saved.getUsername(), saved.getRole()),
            "User registered successfully"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequestDto request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        Map<String, Object> loginData = Map.of(
            "token", token,
            "role", user.getRole(),
            "userId", user.getId(),
            "username", user.getUsername()
        );
        
        return ResponseEntity.ok(ApiResponse.success(loginData, "Login successful"));
    }
}
