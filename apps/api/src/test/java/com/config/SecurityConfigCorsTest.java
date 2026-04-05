package com.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.security.JwtAuthenticationFilter;

class SecurityConfigCorsTest {

    @Test
    void devProfileUsesOriginPatternsForConvenientRemoteDevelopment() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.profiles.active", "dev")
                .withProperty("CORS_ALLOWED_ORIGINS", "https://app.example.com")
                .withProperty("CORS_DEV_ALLOWED_ORIGIN_PATTERNS", "http://192.168.*:*,http://100.*:*");
        SecurityConfig config = new SecurityConfig(mock(JwtAuthenticationFilter.class), environment);

        CorsConfiguration corsConfiguration = getCorsConfiguration(config.corsConfigurationSource());

        assertIterableEquals(List.of("http://192.168.*:*", "http://100.*:*"), corsConfiguration.getAllowedOriginPatterns());
        assertEquals(null, corsConfiguration.getAllowedOrigins());
    }

    @Test
    void nonDevProfilesKeepStrictAllowedOrigins() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.profiles.active", "prod")
                .withProperty("CORS_ALLOWED_ORIGINS", "https://app.example.com,https://admin.example.com")
                .withProperty("CORS_DEV_ALLOWED_ORIGIN_PATTERNS", "http://*:* ");
        SecurityConfig config = new SecurityConfig(mock(JwtAuthenticationFilter.class), environment);

        CorsConfiguration corsConfiguration = getCorsConfiguration(config.corsConfigurationSource());

        assertIterableEquals(List.of("https://app.example.com", "https://admin.example.com"), corsConfiguration.getAllowedOrigins());
        assertEquals(null, corsConfiguration.getAllowedOriginPatterns());
    }

    private static CorsConfiguration getCorsConfiguration(CorsConfigurationSource source) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("OPTIONS");
        request.setRequestURI("/api/auth/register");
        request.addHeader("Origin", "http://100.90.149.52:3001");
        request.addHeader("Access-Control-Request-Method", "POST");
        return source.getCorsConfiguration(request);
    }
}
