package com.stock.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.external.marketdata.MarketDataClient;
import com.stock.entity.Stock;
import com.stock.repository.StockRepository;

@Service
public class PriceUpdateService {

    @Autowired
    private StockRepository stockRepository;

    private final MarketDataClient marketDataClient;

    public PriceUpdateService(MarketDataClient marketDataClient) {
        this.marketDataClient = marketDataClient;
    }

    public void updateAllPrices() {

        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) return;

        String symbols = stocks.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.joining(","));

        Map<String, Double> prices = marketDataClient.getPrices(symbols);

        System.out.println("Prices from FastAPI: " + prices);

        int successCount = 0;
        int failCount = 0;

        for (Stock stock : stocks) {
            try {
                Double newPrice = prices.get(stock.getSymbol());

                if (newPrice == null) {
                    throw new RuntimeException("Price missing for symbol: " + stock.getSymbol());
                }

                stock.setPrice(BigDecimal.valueOf(newPrice));
                successCount++;

            } catch (RuntimeException e) {
                failCount++;
                System.err.println("❌ Failed to update: " + stock.getSymbol());
            }
        }

        // Only save stocks with updated prices
        List<Stock> updatedStocks = stocks.stream()
                .filter(s -> s.getPrice() != null)
                .collect(Collectors.toList());

        stockRepository.saveAll(updatedStocks);

        System.out.println("=== Price Update Summary ===");
        System.out.println("Success: " + successCount);
        System.out.println("Failed: " + failCount);
    }
}