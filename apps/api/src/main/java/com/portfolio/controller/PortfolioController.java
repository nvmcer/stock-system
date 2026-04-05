package com.portfolio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exception.ApiResponse;
import com.portfolio.dto.PortfolioResponseDto;
import com.portfolio.service.PortfolioService;
import com.security.CurrentUserService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final CurrentUserService currentUserService;

    public PortfolioController(PortfolioService portfolioService, CurrentUserService currentUserService) {
        this.portfolioService = portfolioService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortfolioResponseDto>>> getPortfolio(
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        Long resolvedUserId = currentUserService.resolveUserId(authentication, userId);
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getUserPortfolio(resolvedUserId), "Portfolio retrieved successfully"));
    }
}
