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

    public List<PortfolioResponseDto> getUserPortfolio(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);

    return portfolios.stream().map(p -> {

        // price from latest trade
        BigDecimal currentPrice = stockRepository.findById(p.getStock().getId())
        .map(Stock::getPrice)
        .orElse(BigDecimal.ZERO);

        BigDecimal qty = BigDecimal.valueOf(p.getQuantity());

        // unrealized profit/loss  = (currentPrice - avgCost) × qtyuantity
        BigDecimal unrealized = currentPrice
        .subtract(p.getAvgCost())
        .multiply(qty)
        .setScale(2, RoundingMode.HALF_UP);

        // realized profit/loss sum from trades
        BigDecimal realized = p.getRealizedPnl().setScale(2, RoundingMode.HALF_UP);

        // total profit
        BigDecimal total = realized.add(unrealized);

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
}