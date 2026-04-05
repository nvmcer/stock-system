package com.security;

public record AuthenticationContext(Long userId, Object requestDetails) {
}
