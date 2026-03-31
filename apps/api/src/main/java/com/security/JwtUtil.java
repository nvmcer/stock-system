package com.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  private final String SECRET_KEY = "your-very-secret-key-your-very-secret-key"; // Should be at least 256 bits
  private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours
  
  private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

  // Generate a JWT token with username and role claims
  public String generateToken(String username, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role); 
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(key,SignatureAlgorithm.HS256)
        .compact();
  }

  // Validate JWT token by checking username and expiration
  public Boolean validateToken(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
  }

  // Check if token has expired
  private boolean isTokenExpired(String token) {
    Date expiration = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();  
    
    return expiration.before(new Date());
  }

  // Extract username from JWT token
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

  // Extract user role from JWT token
  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  // Generic method to extract any claim from token
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
  }

  // Parse and extract all claims from JWT token
  private Claims extractAllClaims(String token) {
      return Jwts.parserBuilder()
             .setSigningKey(key)
             .build()
             .parseClaimsJws(token)
             .getBody();
  }
}