package com.trades.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.portfolio.entity.Portfolio;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.PortfolioService;
import com.stockManagePortfolio.stockManager.StockManagerApplication;
import com.stock.entity.Stock;
import com.stock.repository.StockRepository;
import com.trades.entity.Trade;
import com.trades.repository.TradeRepository;
import com.user.entity.User;
import com.user.repository.UserRepository;

@ActiveProfiles("test")
@SpringBootTest(
        classes = StockManagerApplication.class,
        properties = "JWT_SECRET=test-jwt-secret-key-with-32-characters")
class UserDashboardReadServiceIntegrationTest {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        portfolioRepository.deleteAll();
        tradeRepository.deleteAll();
        stockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getUserPortfolio_shouldMapLazyLoadedStockDataWhenOpenInViewIsDisabled() {
        User user = userRepository.save(createUser("portfolio-user"));
        Stock stock = stockRepository.save(createStock("AAPL", "Apple Inc.", "210.50"));

        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        portfolio.setStock(stock);
        portfolio.setQuantity(5);
        portfolio.setAvgCost(new BigDecimal("180.00"));
        portfolio.setRealizedPnl(new BigDecimal("25.00"));
        portfolioRepository.save(portfolio);

        var result = portfolioService.getUserPortfolio(user.getId());

        assertEquals(1, result.size());
        assertEquals("AAPL", result.getFirst().getSymbol());
        assertEquals("Apple Inc.", result.getFirst().getName());
        assertEquals(new BigDecimal("210.50"), result.getFirst().getCurrentPrice());
    }

    @Test
    void getTradeHistory_shouldMapLazyLoadedStockDataWhenOpenInViewIsDisabled() {
        User user = userRepository.save(createUser("trade-user"));
        Stock stock = stockRepository.save(createStock("MSFT", "Microsoft Corp.", "320.00"));

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setStock(stock);
        trade.setType("BUY");
        trade.setQuantity(3);
        trade.setPrice(new BigDecimal("300.00"));
        trade.setTimestamp(LocalDateTime.of(2026, 4, 6, 10, 15));
        tradeRepository.save(trade);

        List<com.trades.dto.TradeResponseDto> result = tradeService.getTradeHistory(user.getId());

        assertEquals(1, result.size());
        assertEquals("MSFT", result.getFirst().getStockSymbol());
        assertEquals("Microsoft Corp.", result.getFirst().getStockName());
        assertEquals(LocalDateTime.of(2026, 4, 6, 10, 15), result.getFirst().getTimestamp());
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash("hashed-password");
        user.setRole("ROLE_USER");
        return user;
    }

    private Stock createStock(String symbol, String name, String price) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setName(name);
        stock.setPrice(new BigDecimal(price));
        return stock;
    }
}
