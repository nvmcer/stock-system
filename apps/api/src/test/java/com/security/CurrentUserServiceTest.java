package com.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.user.entity.User;
import com.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CurrentUserService currentUserService;

    @Test
    void resolveUserId_shouldUseJwtUserIdWithoutDatabaseLookup() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "alice", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setDetails(new AuthenticationContext(42L, null));

        Long resolvedUserId = currentUserService.resolveUserId(authentication, null);

        assertEquals(42L, resolvedUserId);
        verify(userService, never()).getByUsername("alice");
    }

    @Test
    void resolveUserId_shouldFallbackToDatabaseLookupWhenLegacyTokenHasNoUserId() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "alice", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        User user = new User();
        user.setId(42L);
        when(userService.getByUsername("alice")).thenReturn(user);

        Long resolvedUserId = currentUserService.resolveUserId(authentication, null);

        assertEquals(42L, resolvedUserId);
        verify(userService).getByUsername("alice");
    }

    @Test
    void resolveUserId_shouldAllowAdminOverride() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        authentication.setDetails(new AuthenticationContext(1L, null));

        Long resolvedUserId = currentUserService.resolveUserId(authentication, 99L);

        assertEquals(99L, resolvedUserId);
        verify(userService, never()).getByUsername("admin");
    }
}
