package com.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.exception.LlmProviderException;
import com.portfolio.dto.PortfolioAnalysisRequestDto;
import com.portfolio.dto.PortfolioAnalysisResponseDto;
import com.portfolio.dto.PortfolioResponseDto;
import com.portfolio.entity.PortfolioAnalysisReport;
import com.portfolio.repository.PortfolioAnalysisReportRepository;
import com.user.entity.User;
import com.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PortfolioAnalysisServiceTest {

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PortfolioAnalysisReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    private PortfolioAnalysisService portfolioAnalysisService;

    @BeforeEach
    void setUp() {
        portfolioAnalysisService = new PortfolioAnalysisService(portfolioService, reportRepository, userRepository, restTemplate);
    }

    @Test
    void generateReport_shouldCallOpenAiCompatibleEndpointAndReturnParsedReport() throws Exception {
        PortfolioResponseDto holding = new PortfolioResponseDto();
        holding.setSymbol("AAPL");
        holding.setName("Apple Inc.");
        holding.setQuantity(10);
        holding.setAvgCost(new BigDecimal("150.00"));
        holding.setCurrentPrice(new BigDecimal("200.00"));
        holding.setRealizedProfit(new BigDecimal("50.00"));
        holding.setUnrealizedProfit(new BigDecimal("500.00"));
        holding.setTotalProfit(new BigDecimal("550.00"));

        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        request.setProvider("OpenAI");
        request.setBaseUrl("https://api.openai.com/v1");
        request.setModel("gpt-4.1-mini");
        request.setApiKey("sk-test");

        String providerResponse = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "```markdown\\n## Overview\\n- Holdings are concentrated in technology\\n```"
                      }
                    }
                  ]
                }
                """;

        when(portfolioService.getUserPortfolio(1L)).thenReturn(List.of(holding));
        when(restTemplate.exchange(
                eq("https://api.openai.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok(providerResponse));
        mockLatestReportSave(1L);

        var response = portfolioAnalysisService.generateReport(1L, request);

        assertEquals("OpenAI", response.getProvider());
        assertEquals("gpt-4.1-mini", response.getModel());
        assertEquals(1, response.getHoldingsAnalyzed());
        assertEquals(new BigDecimal("2000.00"), response.getTotalMarketValue());
        assertEquals("## Overview\n- Holdings are concentrated in technology", response.getReportMarkdown());

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("https://api.openai.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(String.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("Bearer sk-test", headers.getFirst(HttpHeaders.AUTHORIZATION));

        Map<?, ?> requestBody = (Map<?, ?>) entityCaptor.getValue().getBody();
        assertEquals("gpt-4.1-mini", requestBody.get("model"));
        assertEquals(1200, requestBody.get("max_tokens"));

        List<?> messages = (List<?>) requestBody.get("messages");
        Map<?, ?> systemMessage = (Map<?, ?>) messages.get(0);
        Map<?, ?> userMessage = (Map<?, ?>) messages.get(1);
        assertTrue(((String) systemMessage.get("content")).contains("Return valid GitHub-flavored Markdown in English."));
        assertTrue(((String) systemMessage.get("content")).contains("Do not wrap the output in triple backticks."));
        assertTrue(((String) userMessage.get("content")).contains("Generate an analysis report based on the portfolio data below."));

        ArgumentCaptor<PortfolioAnalysisReport> reportCaptor = ArgumentCaptor.forClass(PortfolioAnalysisReport.class);
        verify(reportRepository).save(reportCaptor.capture());
        assertEquals("OpenAI", reportCaptor.getValue().getProvider());
        assertEquals("gpt-4.1-mini", reportCaptor.getValue().getModel());
        assertEquals("## Overview\n- Holdings are concentrated in technology", reportCaptor.getValue().getReportMarkdown());
        assertNotNull(reportCaptor.getValue().getGeneratedAt());
    }

    @Test
    void generateReport_shouldRejectMissingApiKey() {
        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        request.setProvider("OpenAI");
        request.setBaseUrl("https://api.openai.com/v1");
        request.setModel("gpt-4.1-mini");
        request.setApiKey(" ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> portfolioAnalysisService.generateReport(1L, request));

        assertEquals("API key is required.", exception.getMessage());
    }

    @Test
    void generateReport_shouldRejectEmptyPortfolio() {
        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        request.setProvider("OpenAI");
        request.setBaseUrl("https://api.openai.com/v1");
        request.setModel("gpt-4.1-mini");
        request.setApiKey("sk-test");

        when(portfolioService.getUserPortfolio(1L)).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> portfolioAnalysisService.generateReport(1L, request));

        assertEquals("No active holdings available for analysis.", exception.getMessage());
    }

    @Test
    void generateReport_shouldFailWhenProviderResponseHasNoContent() throws Exception {
        PortfolioResponseDto holding = new PortfolioResponseDto();
        holding.setSymbol("AAPL");
        holding.setName("Apple Inc.");
        holding.setQuantity(10);
        holding.setCurrentPrice(new BigDecimal("200.00"));

        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        request.setProvider("OpenAI");
        request.setBaseUrl("https://api.openai.com/v1");
        request.setModel("gpt-4.1-mini");
        request.setApiKey("sk-test");

        when(portfolioService.getUserPortfolio(1L)).thenReturn(List.of(holding));
        when(restTemplate.exchange(
                eq("https://api.openai.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok("{" + "\"choices\":[{}]}"));

        LlmProviderException exception = assertThrows(
                LlmProviderException.class,
                () -> portfolioAnalysisService.generateReport(1L, request));

        assertEquals("LLM provider returned an empty analysis report.", exception.getMessage());
    }

    @Test
    void generateReport_shouldSupportContentArrayResponses() throws Exception {
        PortfolioResponseDto holding = new PortfolioResponseDto();
        holding.setSymbol("NVDA");
        holding.setName("NVIDIA Corp.");
        holding.setQuantity(4);
        holding.setCurrentPrice(new BigDecimal("100.00"));

        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        request.setProvider("OpenRouter");
        request.setBaseUrl("https://openrouter.ai/api/v1");
        request.setModel("openai/gpt-4.1-mini");
        request.setLanguage("zh-CN");
        request.setApiKey("sk-test");

        when(portfolioService.getUserPortfolio(1L)).thenReturn(List.of(holding));
        when(restTemplate.exchange(
                eq("https://openrouter.ai/api/v1/chat/completions"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok("""
                        {
                          "choices": [
                            {
                              "message": {
                                "content": [
                                  { "type": "text", "text": "## 总览" },
                                  { "type": "text", "text": "- 半导体权重较高" }
                                ]
                              }
                            }
                          ]
                        }
                        """));
        mockLatestReportSave(1L);

        var response = portfolioAnalysisService.generateReport(1L, request);

        assertEquals("## 总览\n- 半导体权重较高", response.getReportMarkdown());

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("https://openrouter.ai/api/v1/chat/completions"),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(String.class));

        Map<?, ?> requestBody = (Map<?, ?>) entityCaptor.getValue().getBody();
        List<?> messages = (List<?>) requestBody.get("messages");
        Map<?, ?> systemMessage = (Map<?, ?>) messages.get(0);
        Map<?, ?> userMessage = (Map<?, ?>) messages.get(1);
        assertTrue(((String) systemMessage.get("content")).contains("Return valid GitHub-flavored Markdown in Simplified Chinese."));
        assertTrue(((String) systemMessage.get("content")).contains("Do not turn section headings into numbered or bulleted list items."));
        assertTrue(((String) userMessage.get("content")).contains("请基于下面的持仓数据生成分析报告。"));
    }

    @Test
    void getLatestReport_shouldReturnMappedSavedReport() {
        PortfolioAnalysisReport report = new PortfolioAnalysisReport();
        report.setProvider("DeepSeek");
        report.setModel("deepseek-chat");
        report.setReportMarkdown("## 总览\n- 已保存报告");
        report.setGeneratedAt(Instant.parse("2026-04-06T03:00:00Z"));
        report.setHoldingsAnalyzed(2);
        report.setTotalMarketValue(new BigDecimal("1234.56"));

        when(reportRepository.findByUserId(1L)).thenReturn(Optional.of(report));

        PortfolioAnalysisResponseDto latestReport = portfolioAnalysisService.getLatestReport(1L);

        assertEquals("DeepSeek", latestReport.getProvider());
        assertEquals("deepseek-chat", latestReport.getModel());
        assertEquals("## 总览\n- 已保存报告", latestReport.getReportMarkdown());
        assertEquals("2026-04-06T03:00:00Z", latestReport.getGeneratedAt());
        assertEquals(2, latestReport.getHoldingsAnalyzed());
        assertEquals(new BigDecimal("1234.56"), latestReport.getTotalMarketValue());
    }

    @Test
    void generateReport_shouldRaiseProviderExceptionWhenProviderIsUnreachable() {
        PortfolioResponseDto holding = new PortfolioResponseDto();
        holding.setSymbol("AAPL");
        holding.setName("Apple Inc.");
        holding.setQuantity(10);
        holding.setCurrentPrice(new BigDecimal("200.00"));

        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        request.setProvider("OpenAI");
        request.setBaseUrl("https://api.openai.com/v1");
        request.setModel("gpt-4.1-mini");
        request.setApiKey("sk-test");

        when(portfolioService.getUserPortfolio(1L)).thenReturn(List.of(holding));
        when(restTemplate.exchange(
                eq("https://api.openai.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenThrow(new ResourceAccessException("Connection timed out"));

        LlmProviderException exception = assertThrows(
                LlmProviderException.class,
                () -> portfolioAnalysisService.generateReport(1L, request));

        assertEquals(
                "Failed to reach the selected LLM provider. Verify the base URL, API compatibility, and network access.",
                exception.getMessage());
    }

    @Test
    void generateReport_shouldRejectUnreadableProviderResponse() {
        PortfolioResponseDto holding = new PortfolioResponseDto();
        holding.setSymbol("AAPL");
        holding.setName("Apple Inc.");
        holding.setQuantity(10);
        holding.setCurrentPrice(new BigDecimal("200.00"));

        PortfolioAnalysisRequestDto request = new PortfolioAnalysisRequestDto();
        request.setProvider("DeepSeek");
        request.setBaseUrl("https://api.deepseek.com/v1");
        request.setModel("deepseek-chat");
        request.setApiKey("sk-test");

        when(portfolioService.getUserPortfolio(1L)).thenReturn(List.of(holding));
        when(restTemplate.exchange(
                eq("https://api.deepseek.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok("not-json"));

        LlmProviderException exception = assertThrows(
                LlmProviderException.class,
                () -> portfolioAnalysisService.generateReport(1L, request));

        assertEquals("LLM provider returned an unreadable response.", exception.getMessage());
    }

    private void mockLatestReportSave(Long userId) {
        User user = new User();
        user.setId(userId);

        when(reportRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(reportRepository.save(any(PortfolioAnalysisReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
