package com.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.user.entity.User;
import com.user.service.UserService;

@Service
public class CurrentUserService {

    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    public Long resolveUserId(Authentication authentication, Long requestedUserId) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Authenticated user context is required");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin && requestedUserId != null) {
            return requestedUserId;
        }

        if (authentication.getDetails() instanceof AuthenticationContext context && context.userId() != null) {
            return context.userId();
        }

        User currentUser = userService.getByUsername(authentication.getName());
        return currentUser.getId();
    }
}
