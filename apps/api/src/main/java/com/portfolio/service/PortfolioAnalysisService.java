package com.portfolio.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.exception.LlmProviderException;
import com.portfolio.dto.PortfolioAnalysisRequestDto;
import com.portfolio.dto.PortfolioAnalysisResponseDto;
import com.portfolio.dto.PortfolioResponseDto;
import com.portfolio.entity.PortfolioAnalysisReport;
import com.portfolio.repository.PortfolioAnalysisReportRepository;
import com.user.repository.UserRepository;

@Service
public class PortfolioAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioAnalysisService.class);
    private static final String DEFAULT_PROVIDER_NAME = "Custom";
    private static final String REPORT_LANGUAGE_ENGLISH = "en";
    private static final String REPORT_LANGUAGE_SIMPLIFIED_CHINESE = "zh-CN";

    private final PortfolioService portfolioService;
    private final PortfolioAnalysisReportRepository reportRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PortfolioAnalysisService(
            PortfolioService portfolioService,
            PortfolioAnalysisReportRepository reportRepository,
            UserRepository userRepository,
            RestTemplate restTemplate) {
        this.portfolioService = portfolioService;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public PortfolioAnalysisResponseDto generateReport(Long userId, PortfolioAnalysisRequestDto request) {
        validateRequest(request);

        List<PortfolioResponseDto> holdings = portfolioService.getUserPortfolio(userId);
        if (holdings.isEmpty()) {
            throw new IllegalArgumentException("No active holdings available for analysis.");
        }

        BigDecimal totalMarketValue = holdings.stream()
                .map(this::calculateMarketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        String providerName = resolveProviderName(request);
        String providerUrl = buildProviderUrl(request.getBaseUrl());
        String model = request.getModel().trim();
        String reportLanguage = resolveReportLanguage(request.getLanguage());
        Instant generatedAt = Instant.now();

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.2,
                "max_tokens", 1200,
                "messages", List.of(
                        Map.of("role", "system", "content", buildSystemPrompt(reportLanguage)),
                        Map.of("role", "user", "content", buildUserPrompt(holdings, totalMarketValue, reportLanguage))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(request.getApiKey().trim());

        try {
            log.info("Generating portfolio analysis via provider {} using model {}", providerName, model);
            ResponseEntity<String> response = restTemplate.exchange(
                    providerUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    String.class);

            PortfolioAnalysisResponseDto result = new PortfolioAnalysisResponseDto();
            result.setProvider(providerName);
            result.setModel(model);
            result.setReportMarkdown(extractReportMarkdown(response.getBody()));
            result.setGeneratedAt(generatedAt.toString());
            result.setHoldingsAnalyzed(holdings.size());
            result.setTotalMarketValue(totalMarketValue);
            return saveLatestReport(userId, result, generatedAt);
        } catch (HttpStatusCodeException ex) {
            throw new IllegalArgumentException("LLM provider rejected the request: "
                    + extractProviderErrorMessage(ex.getResponseBodyAsString()));
        } catch (RestClientException ex) {
            log.error("Failed to reach LLM provider: {}", ex.getMessage());
            throw new LlmProviderException(
                    "Failed to reach the selected LLM provider. Verify the base URL, API compatibility, and network access.");
        }
    }

    public PortfolioAnalysisResponseDto getLatestReport(Long userId) {
        return reportRepository.findByUserId(userId)
                .map(this::toResponseDto)
                .orElse(null);
    }

    private void validateRequest(PortfolioAnalysisRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Analysis request is required.");
        }
        if (isBlank(request.getBaseUrl())) {
            throw new IllegalArgumentException("Base URL is required.");
        }
        if (isBlank(request.getModel())) {
            throw new IllegalArgumentException("Model is required.");
        }
        if (isBlank(request.getApiKey())) {
            throw new IllegalArgumentException("API key is required.");
        }
    }

    private String buildProviderUrl(String baseUrl) {
        String normalized = baseUrl.trim();
        if (!(normalized.startsWith("https://") || normalized.startsWith("http://"))) {
            throw new IllegalArgumentException("Base URL must start with http:// or https://.");
        }

        try {
            URI.create(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Base URL must be a valid URL.");
        }

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (normalized.endsWith("/chat/completions")) {
            return normalized;
        }
        return normalized + "/chat/completions";
    }

    private String resolveProviderName(PortfolioAnalysisRequestDto request) {
        return isBlank(request.getProvider()) ? DEFAULT_PROVIDER_NAME : request.getProvider().trim();
    }

    private String resolveReportLanguage(String language) {
        if (isBlank(language)) {
            return REPORT_LANGUAGE_ENGLISH;
        }

        String normalized = language.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        return switch (normalized) {
            case "zh", "zh-cn", "zh-hans", "chinese", "simplified-chinese" -> REPORT_LANGUAGE_SIMPLIFIED_CHINESE;
            default -> REPORT_LANGUAGE_ENGLISH;
        };
    }

    private String buildSystemPrompt(String language) {
        if (REPORT_LANGUAGE_SIMPLIFIED_CHINESE.equals(language)) {
            return """
                    You are a professional portfolio risk analyst.
                    Analyze only the portfolio data provided by the user.
                    Infer the most likely industry exposure for each holding from its symbol and company name when sector data is not explicit.
                    If an industry classification is uncertain, explicitly label it as an assumption.
                    Return valid GitHub-flavored Markdown in Simplified Chinese.
                    Return only the report body. Do not wrap the output in triple backticks.
                    Do not turn section headings into numbered or bulleted list items.
                    Use exactly these Markdown headings in this order:
                    ## 总览
                    ## 产业暴露度
                    ## 持仓结构
                    ## 风险分析
                    ## 关注事项与建议
                    Keep the tone concise, practical, and data-backed.
                    """;
        }

        return """
                You are a professional portfolio risk analyst.
                Analyze only the portfolio data provided by the user.
                Infer the most likely industry exposure for each holding from its symbol and company name when sector data is not explicit.
                If an industry classification is uncertain, explicitly label it as an assumption.
                Return valid GitHub-flavored Markdown in English.
                Return only the report body. Do not wrap the output in triple backticks.
                Do not turn section headings into numbered or bulleted list items.
                Use exactly these Markdown headings in this order:
                ## Overview
                ## Industry Exposure
                ## Holdings Structure
                ## Risk Analysis
                ## Watchlist and Suggestions
                Keep the tone concise, practical, and data-backed.
                """;
    }

    private String buildUserPrompt(List<PortfolioResponseDto> holdings, BigDecimal totalMarketValue, String language) {
        StringBuilder prompt = new StringBuilder();

        if (REPORT_LANGUAGE_SIMPLIFIED_CHINESE.equals(language)) {
            prompt.append("请基于下面的持仓数据生成分析报告。\n");
            prompt.append("总持仓市值（按 currentPrice * quantity 估算）: ")
                    .append(totalMarketValue.toPlainString())
                    .append("\n");
            prompt.append("活跃持仓数量: ").append(holdings.size()).append("\n");
            prompt.append("当前没有现金仓位、基准指数、波动率或行业标签等额外数据。\n");
            prompt.append("请明确指出哪些产业归类属于基于证券名称/代码的推断。\n\n");
            prompt.append("持仓明细:\n");
        } else {
            prompt.append("Generate an analysis report based on the portfolio data below.\n");
            prompt.append("Total portfolio market value (estimated by currentPrice * quantity): ")
                    .append(totalMarketValue.toPlainString())
                    .append("\n");
            prompt.append("Active holdings count: ").append(holdings.size()).append("\n");
            prompt.append("There is currently no additional data such as cash position, benchmark index, volatility, or sector labels.\n");
            prompt.append("Explicitly identify which industry classifications are inferred from security names or symbols.\n\n");
            prompt.append("Holdings:\n");
        }

        for (PortfolioResponseDto holding : holdings) {
            BigDecimal marketValue = calculateMarketValue(holding);
            BigDecimal weight = calculateWeightPercentage(marketValue, totalMarketValue);

            prompt.append("- symbol: ").append(nullToEmpty(holding.getSymbol())).append("\n");
            prompt.append("  name: ").append(nullToEmpty(holding.getName())).append("\n");
            prompt.append("  quantity: ").append(holding.getQuantity() == null ? 0 : holding.getQuantity()).append("\n");
            prompt.append("  avgCost: ").append(amountOrZero(holding.getAvgCost()).toPlainString()).append("\n");
            prompt.append("  currentPrice: ").append(amountOrZero(holding.getCurrentPrice()).toPlainString()).append("\n");
            prompt.append("  marketValue: ").append(marketValue.toPlainString()).append("\n");
            prompt.append("  portfolioWeightPct: ").append(weight.toPlainString()).append("\n");
            prompt.append("  realizedProfit: ").append(amountOrZero(holding.getRealizedProfit()).toPlainString()).append("\n");
            prompt.append("  unrealizedProfit: ").append(amountOrZero(holding.getUnrealizedProfit()).toPlainString()).append("\n");
            prompt.append("  totalProfit: ").append(amountOrZero(holding.getTotalProfit()).toPlainString()).append("\n");
        }

        if (REPORT_LANGUAGE_SIMPLIFIED_CHINESE.equals(language)) {
            prompt.append("\n请重点分析产业暴露度、集中度、盈亏结构、潜在风险，并给出简洁的观察结论。\n");
        } else {
            prompt.append("\nFocus on industry exposure, concentration, profit and loss structure, potential risks, and concise conclusions.\n");
        }

        return prompt.toString();
    }

    private BigDecimal calculateMarketValue(PortfolioResponseDto holding) {
        BigDecimal quantity = BigDecimal.valueOf(holding.getQuantity() == null ? 0 : holding.getQuantity());
        return amountOrZero(holding.getCurrentPrice())
                .multiply(quantity)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateWeightPercentage(BigDecimal marketValue, BigDecimal totalMarketValue) {
        if (totalMarketValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return marketValue.multiply(BigDecimal.valueOf(100))
                .divide(totalMarketValue, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal amountOrZero(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String extractReportMarkdown(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new LlmProviderException("LLM provider returned an empty response.");
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException ex) {
            throw new LlmProviderException("LLM provider returned an unreadable response.");
        }

        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        String content = extractContentText(contentNode).trim();

        if (content.isBlank()) {
            content = root.path("choices").path(0).path("text").asText("").trim();
        }

        content = unwrapMarkdownCodeFence(content);

        if (content.isBlank()) {
            throw new LlmProviderException("LLM provider returned an empty analysis report.");
        }

        return content;
    }

    private String extractContentText(JsonNode contentNode) {
        if (contentNode == null || contentNode.isMissingNode() || contentNode.isNull()) {
            return "";
        }
        if (contentNode.isTextual()) {
            return contentNode.asText("");
        }
        if (contentNode.isObject()) {
            return contentNode.path("text").asText("");
        }
        if (!contentNode.isArray()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (JsonNode item : contentNode) {
            String textPart = item.isTextual() ? item.asText("") : item.path("text").asText("");
            if (textPart.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append('\n');
            }
            builder.append(textPart.trim());
        }
        return builder.toString();
    }

    private String extractProviderErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "please verify the API key, model, and base URL.";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String message = root.path("error").path("message").asText("").trim();
            return message.isBlank() ? "please verify the API key, model, and base URL." : message;
        } catch (JsonProcessingException ex) {
            return "please verify the API key, model, and base URL.";
        }
    }

    private String unwrapMarkdownCodeFence(String content) {
        String normalized = content.trim().replace("\r\n", "\n");
        int firstLineBreak = normalized.indexOf('\n');

        if (firstLineBreak < 0 || !normalized.endsWith("```")) {
            return content.trim();
        }

        String openingFence = normalized.substring(0, firstLineBreak).trim().toLowerCase(Locale.ROOT);
        if (!(openingFence.equals("```")
                || openingFence.equals("```markdown")
                || openingFence.equals("```md"))) {
            return content.trim();
        }

        return normalized.substring(firstLineBreak + 1, normalized.length() - 3).trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private PortfolioAnalysisResponseDto saveLatestReport(
            Long userId,
            PortfolioAnalysisResponseDto generatedReport,
            Instant generatedAt) {
        PortfolioAnalysisReport savedReport = reportRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PortfolioAnalysisReport report = new PortfolioAnalysisReport();
                    report.setUser(userRepository.getReferenceById(userId));
                    return report;
                });

        savedReport.setProvider(generatedReport.getProvider());
        savedReport.setModel(generatedReport.getModel());
        savedReport.setReportMarkdown(generatedReport.getReportMarkdown());
        savedReport.setGeneratedAt(generatedAt);
        savedReport.setHoldingsAnalyzed(generatedReport.getHoldingsAnalyzed());
        savedReport.setTotalMarketValue(generatedReport.getTotalMarketValue());

        return toResponseDto(reportRepository.save(savedReport));
    }

    private PortfolioAnalysisResponseDto toResponseDto(PortfolioAnalysisReport report) {
        PortfolioAnalysisResponseDto dto = new PortfolioAnalysisResponseDto();
        dto.setProvider(report.getProvider());
        dto.setModel(report.getModel());
        dto.setReportMarkdown(report.getReportMarkdown());
        dto.setGeneratedAt(report.getGeneratedAt().toString());
        dto.setHoldingsAnalyzed(report.getHoldingsAnalyzed());
        dto.setTotalMarketValue(report.getTotalMarketValue());
        return dto;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
