package com.portfolio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exception.ApiResponse;
import com.portfolio.dto.PortfolioAnalysisRequestDto;
import com.portfolio.dto.PortfolioAnalysisResponseDto;
import com.portfolio.dto.PortfolioResponseDto;
import com.portfolio.service.PortfolioAnalysisService;
import com.portfolio.service.PortfolioService;
import com.security.CurrentUserService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioAnalysisService portfolioAnalysisService;
    private final CurrentUserService currentUserService;

    public PortfolioController(
            PortfolioService portfolioService,
            PortfolioAnalysisService portfolioAnalysisService,
            CurrentUserService currentUserService) {
        this.portfolioService = portfolioService;
        this.portfolioAnalysisService = portfolioAnalysisService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortfolioResponseDto>>> getPortfolio(
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        Long resolvedUserId = currentUserService.resolveUserId(authentication, userId);
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getUserPortfolio(resolvedUserId), "Portfolio retrieved successfully"));
    }

    @GetMapping("/analysis-report/latest")
    public ResponseEntity<ApiResponse<PortfolioAnalysisResponseDto>> getLatestPortfolioAnalysisReport(
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        Long resolvedUserId = currentUserService.resolveUserId(authentication, userId);
        PortfolioAnalysisResponseDto latestReport = portfolioAnalysisService.getLatestReport(resolvedUserId);
        String message = latestReport == null
                ? "No saved portfolio analysis report found"
                : "Latest portfolio analysis report retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(latestReport, message));
    }

    @PostMapping("/analysis-report")
    public ResponseEntity<ApiResponse<PortfolioAnalysisResponseDto>> generatePortfolioAnalysisReport(
            @RequestParam(required = false) Long userId,
            @RequestBody PortfolioAnalysisRequestDto request,
            Authentication authentication) {
        Long resolvedUserId = currentUserService.resolveUserId(authentication, userId);
        return ResponseEntity.ok(ApiResponse.success(
                portfolioAnalysisService.generateReport(resolvedUserId, request),
                "Portfolio analysis report generated successfully"));
    }
}
