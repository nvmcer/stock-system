package com.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.repository.PortfolioRepository;
import com.trades.repository.TradeRepository;
import com.user.entity.User;
import com.user.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  
  @Autowired
  private PortfolioRepository portfolioRepository;
  
  @Autowired
  private TradeRepository tradeRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  // Register a new user with validation
  public User register(String username, String rawPassword) {
    // Check if username already exists
    if (userRepository.findByUsername(username).isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }

    // Validate password length
    if (rawPassword == null || rawPassword.length() < 6) {
      throw new IllegalArgumentException("Password must be at least 6 characters long");
    }

    // Create new user with hashed password
    User user = new User();
    user.setUsername(username);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setRole("ROLE_USER"); // Default role for new users
    return userRepository.save(user);
  }

  // Authenticate user with username and password
  public User login(String username, String rawPassword) {
    // Find user by username
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Username not found"));

    // Verify password matches the stored hash
    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid password");
    }

    return user;
  }

  // Get all users (excluding admin users)
  public List<User> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .filter(user -> !user.getRole().equals("ROLE_ADMIN"))
        .collect(Collectors.toList());
  }

  // Delete user by ID with cascade delete for related records
  @Transactional
  public void deleteUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    // Delete all trades for this user
    tradeRepository.deleteAll(tradeRepository.findByUserId(userId));

    // Delete all portfolio entries for this user
    portfolioRepository.deleteAll(portfolioRepository.findByUserId(userId));

    // Delete the user
    userRepository.deleteById(userId);
  }
}

