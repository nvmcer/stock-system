package com.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
    User saved = userService.register(request.getUsername(), request.getPassword());
    return ResponseEntity.ok(new UserResponseDto(saved.getId(), saved.getUsername()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
    User user = userService.login(request.getUsername(), request.getPassword());
    String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
    return ResponseEntity.ok(Map.of(
      "message", "Login successful", 
      "token", token,
      "role",user.getRole(),
      "userId", user.getId(),
      "username", user.getUsername()
    ));
  }
}