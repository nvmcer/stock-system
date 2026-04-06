package com.portfolio.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.portfolio.dto.PortfolioResponseDto;
import com.portfolio.dto.PortfolioAnalysisRequestDto;
import com.portfolio.dto.PortfolioAnalysisResponseDto;
import com.portfolio.service.PortfolioAnalysisService;
import com.portfolio.service.PortfolioService;
import com.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTest {

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private PortfolioAnalysisService portfolioAnalysisService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Authentication authentication;

    private PortfolioController portfolioController;

    @BeforeEach
    void setUp() {
        portfolioController = new PortfolioController(portfolioService, portfolioAnalysisService, currentUserService);
    }

    @Test
    void getPortfolio_shouldUseResolvedUserId() {
        when(currentUserService.resolveUserId(authentication, 999L)).thenReturn(1L);
        when(portfolioService.getUserPortfolio(1L)).thenReturn(List.of(new PortfolioResponseDto()));

        var response = portfolioController.getPortfolio(999L, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(portfolioService).getUserPortfolio(1L);
    }

    @Test
    void getLatestPortfolioAnalysisReport_shouldUseResolvedUserId() {
        PortfolioAnalysisResponseDto result = new PortfolioAnalysisResponseDto();

        when(currentUserService.resolveUserId(authentication, 999L)).thenReturn(1L);
        when(portfolioAnalysisService.getLatestReport(1L)).thenReturn(result);

        var response = portfolioController.getLatestPortfolioAnalysisReport(999L, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(portfolioAnalysisService).getLatestReport(1L);
    }

    @Test
    void generatePortfolioAnalysisReport_shouldUseResolvedUserId() {
        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        PortfolioAnalysisResponseDto result = new PortfolioAnalysisResponseDto();

        when(currentUserService.resolveUserId(authentication, 999L)).thenReturn(1L);
        when(portfolioAnalysisService.generateReport(1L, request)).thenReturn(result);

        var response = portfolioController.generatePortfolioAnalysisReport(999L, request, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(portfolioAnalysisService).generateReport(1L, request);
    }
}
