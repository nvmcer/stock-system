package com.trades.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.entity.Portfolio;
import com.portfolio.repository.PortfolioRepository;
import com.stock.entity.Stock;
import com.stock.repository.StockRepository;
import com.trades.dto.TradeRequestDto;
import com.trades.dto.TradeResponseDto;
import com.trades.entity.Trade;
import com.trades.repository.TradeRepository;
import com.user.entity.User;
import com.user.repository.UserRepository;

@Service
public class TradeService {
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    public TradeService(TradeRepository tradeRepository, StockRepository stockRepository, UserRepository userRepository, PortfolioRepository portfolioRepository) {
        this.tradeRepository = tradeRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Transactional
    public Trade executeTrade(Long userId, TradeRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow();
        Stock stock = stockRepository.findBySymbol(request.getSymbol()).orElseThrow();

        // save trade record
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStock(stock);
        trade.setType(request.getType());
        trade.setQuantity(request.getQuantity());
        trade.setPrice(request.getPrice());
        tradeRepository.save(trade);

        // update portfolio
        Portfolio portfolio = portfolioRepository.findByUserIdAndStockId(userId, stock.getId())
                .orElse(null);

        if ("BUY".equalsIgnoreCase(request.getType())) {
            if (portfolio == null) {
                // create new portfolio
                portfolio = new Portfolio();
                portfolio.setUser(user);
                portfolio.setStock(stock);
                portfolio.setQuantity(request.getQuantity());
                portfolio.setAvgCost(request.getPrice());
            } else {
                // update existing portfolio
                int oldQty = portfolio.getQuantity();
                BigDecimal oldCost = portfolio.getAvgCost().multiply(BigDecimal.valueOf(oldQty));

                int newQty = oldQty + request.getQuantity();
                BigDecimal newCost = oldCost.add(request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

                BigDecimal newAvgCost = newCost.divide(BigDecimal.valueOf(newQty), RoundingMode.HALF_UP);

                portfolio.setQuantity(newQty);
                portfolio.setAvgCost(newAvgCost);
            }
            portfolioRepository.save(portfolio);

        } else if ("SELL".equalsIgnoreCase(request.getType())) {
            if (portfolio == null || portfolio.getQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException("Insufficient shares to sell");
            }

            int requestQty = request.getQuantity();
            int oldQty = portfolio.getQuantity() - requestQty;

            portfolio.setQuantity(oldQty);

            BigDecimal realizedDelta = request.getPrice()
                    .subtract(portfolio.getAvgCost())
                    .multiply(BigDecimal.valueOf(requestQty)); 

            portfolio.setRealizedPnl(
                    portfolio.getRealizedPnl().add(realizedDelta)
            );

            if (oldQty == 0) {
                portfolioRepository.delete(portfolio);
            } else {
                portfolioRepository.save(portfolio);
            }
        }

        return trade;
    }

    public List<TradeResponseDto> getTradeHistory(Long userId) {
        List<Trade> trades = tradeRepository.findByUserId(userId);
        return trades.stream().map(t -> {
            TradeResponseDto dto = new TradeResponseDto();
            dto.setId(t.getId());
            dto.setType(t.getType());
            dto.setQuantity(t.getQuantity());
            dto.setPrice(t.getPrice());
            dto.setStockSymbol(t.getStock().getSymbol());
            dto.setStockName(t.getStock().getName());
            dto.setTimestamp(t.getTimestamp());
            return dto;
        }).collect(Collectors.toList());
    }
}