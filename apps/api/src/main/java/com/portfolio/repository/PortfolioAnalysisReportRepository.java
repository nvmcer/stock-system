package com.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.entity.PortfolioAnalysisReport;

public interface PortfolioAnalysisReportRepository extends JpaRepository<PortfolioAnalysisReport, Long> {
    Optional<PortfolioAnalysisReport> findByUserId(Long userId);
}
