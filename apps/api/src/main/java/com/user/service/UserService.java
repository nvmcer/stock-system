package com.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.repository.PortfolioRepository;
import com.trades.repository.TradeRepository;
import com.user.entity.User;
import com.user.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final PortfolioRepository portfolioRepository;
  private final TradeRepository tradeRepository;

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      PortfolioRepository portfolioRepository,
      TradeRepository tradeRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.portfolioRepository = portfolioRepository;
    this.tradeRepository = tradeRepository;
  }

  public User register(String username, String rawPassword) {
    if (userRepository.findByUsername(username).isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }

    if (rawPassword == null || rawPassword.length() < 6) {
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

  public List<User> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .filter(user -> !user.getRole().equals("ROLE_ADMIN"))
        .collect(Collectors.toList());
  }

  public User getByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
  }

  @Transactional
  public void deleteUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    tradeRepository.deleteAll(tradeRepository.findByUserId(userId));
    portfolioRepository.deleteAll(portfolioRepository.findByUserId(userId));
    userRepository.deleteById(userId);
  }
}
