package com.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class TestController {
        @GetMapping("/test/auth-exception")
        void throwAuthenticationException() {
            throw new BadCredentialsException("Invalid credentials");
        }

        @GetMapping("/test/access-denied")
        void throwAccessDeniedException() {
            throw new AccessDeniedException("Access denied");
        }

        @GetMapping("/test/user-not-found")
        void throwUsernameNotFoundException() {
            throw new UsernameNotFoundException("User not found");
        }

        @GetMapping("/test/illegal-argument")
        void throwIllegalArgumentException() {
            throw new IllegalArgumentException("Invalid parameter");
        }

        @GetMapping("/test/entity-not-found")
        void throwEntityNotFoundException() {
            throw new EntityNotFoundException("Entity not found");
        }

        @GetMapping("/test/generic-exception")
        void throwGenericException() {
            throw new RuntimeException("Something went wrong");
        }

        @GetMapping("/test/llm-provider-exception")
        void throwLlmProviderException() {
            throw new LlmProviderException("Provider network timeout");
        }
    }

    @Test
    void handleAuthenticationException_shouldReturnUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/test/auth-exception"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("Unauthorized: Invalid credentials"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleAccessDeniedException_shouldReturnForbiddenResponse() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(jsonPath("$.message").value("Forbidden: Access denied"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleUsernameNotFoundException_shouldReturnNotFoundResponse() throws Exception {
        mockMvc.perform(get("/test/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequestResponse() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Invalid parameter"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleEntityNotFoundException_shouldReturnNotFoundResponse() throws Exception {
        mockMvc.perform(get("/test/entity-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerErrorResponse() throws Exception {
        mockMvc.perform(get("/test/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.message").value("Internal Server Error: Something went wrong"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleLlmProviderException_shouldReturnBadGatewayResponse() throws Exception {
        mockMvc.perform(get("/test/llm-provider-exception"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("502"))
                .andExpect(jsonPath("$.message").value("Provider network timeout"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
