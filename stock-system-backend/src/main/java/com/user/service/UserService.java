package com.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.entity.User;
import com.user.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public User register(String username, String rawPassword) {

    if(userRepository.findByUsername(username).isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }

    if(rawPassword == null || rawPassword.length() < 6) {
      throw new IllegalArgumentException("Password must be at least 6 characters long");
    }

    User user = new User();
    user.setUsername(username);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setRole("ROLE_USER");
    return userRepository.save(user);
  }

  public User login(String username, String rawPassword) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Username not found"));

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid password");
    }
    
    return user;
  }
}