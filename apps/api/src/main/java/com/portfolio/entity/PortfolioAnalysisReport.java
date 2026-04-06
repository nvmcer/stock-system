package com.portfolio.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "portfolio_analysis_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id" })
})
public class PortfolioAnalysisReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 80)
    private String provider;

    @Column(nullable = false, length = 120)
    private String model;

    @Column(name = "report_markdown", nullable = false, columnDefinition = "TEXT")
    private String reportMarkdown;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @Column(name = "holdings_analyzed", nullable = false)
    private Integer holdingsAnalyzed;

    @Column(name = "total_market_value", nullable = false)
    private BigDecimal totalMarketValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
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
