package com.portfolio.dto;

import java.math.BigDecimal;

public class PortfolioAnalysisResponseDto {
    private String provider;
    private String model;
    private String reportMarkdown;
    private String generatedAt;
    private Integer holdingsAnalyzed;
    private BigDecimal totalMarketValue;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getReportMarkdown() {
        return reportMarkdown;
    }

    public void setReportMarkdown(String reportMarkdown) {
        this.reportMarkdown = reportMarkdown;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Integer getHoldingsAnalyzed() {
        return holdingsAnalyzed;
    }

    public void setHoldingsAnalyzed(Integer holdingsAnalyzed) {
        this.holdingsAnalyzed = holdingsAnalyzed;
    }

    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }
}
