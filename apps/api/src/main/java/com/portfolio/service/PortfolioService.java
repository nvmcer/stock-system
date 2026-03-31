package com.portfolio.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.dto.PortfolioResponseDto;
import com.portfolio.entity.Portfolio;
import com.portfolio.repository.PortfolioRepository;
import com.stock.entity.Stock;
import com.stock.repository.StockRepository;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    @Autowired
    private StockRepository stockRepository;


    public PortfolioService(PortfolioRepository portfolioRepository, StockRepository stockRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    // Get user's portfolio with profit/loss calculations (only active holdings)
    public List<PortfolioResponseDto> getUserPortfolio(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);

        return portfolios.stream()
            // Only show active holdings (quantity > 0)
            .filter(p -> p.getQuantity() > 0)
            .map(p -> {

            // Get current price from latest stock price
            BigDecimal currentPrice = stockRepository.findById(p.getStock().getId())
            .map(Stock::getPrice)
            .orElse(BigDecimal.ZERO);

            BigDecimal qty = BigDecimal.valueOf(p.getQuantity());

            // Calculate unrealized profit/loss = (currentPrice - avgCost) × quantity
            BigDecimal unrealized = currentPrice
            .subtract(p.getAvgCost())
            .multiply(qty)
            .setScale(2, RoundingMode.HALF_UP);

            // Get realized profit/loss from trade history
            BigDecimal realized = p.getRealizedPnl().setScale(2, RoundingMode.HALF_UP);

            // Total profit = realized + unrealized
            BigDecimal total = realized.add(unrealized);

            // Build response DTO
            PortfolioResponseDto dto = new PortfolioResponseDto();
            dto.setSymbol(p.getStock().getSymbol());
            dto.setName(p.getStock().getName());
            dto.setQuantity(p.getQuantity());
            dto.setAvgCost(p.getAvgCost().setScale(2, RoundingMode.HALF_UP));
            dto.setCurrentPrice(currentPrice.setScale(2, RoundingMode.HALF_UP));
            dto.setRealizedProfit(realized);
            dto.setUnrealizedProfit(unrealized);
            dto.setTotalProfit(total);

            return dto;
        }).collect(Collectors.toList());
    }
    
    // Calculate total profit including cleared positions
    public BigDecimal getTotalProfit(Long userId) {
        List<Portfolio> allPortfolios = portfolioRepository.findByUserId(userId);
        
        BigDecimal totalProfit = BigDecimal.ZERO;
        
        // Sum profit from all portfolios (both active and cleared positions)
        for (Portfolio portfolio : allPortfolios) {
            // Get current price for active positions
            BigDecimal currentPrice = stockRepository.findById(portfolio.getStock().getId())
                .map(Stock::getPrice)
                .orElse(BigDecimal.ZERO);
            
            BigDecimal qty = BigDecimal.valueOf(portfolio.getQuantity());
            
            // Unrealized profit for active positions
            BigDecimal unrealized = currentPrice
                .subtract(portfolio.getAvgCost())
                .multiply(qty)
                .setScale(2, RoundingMode.HALF_UP);
            
            // Realized profit from trades
            BigDecimal realized = portfolio.getRealizedPnl().setScale(2, RoundingMode.HALF_UP);
            
            // Add to total
            totalProfit = totalProfit.add(realized).add(unrealized);
        }
        
        return totalProfit.setScale(2, RoundingMode.HALF_UP);
    }
}