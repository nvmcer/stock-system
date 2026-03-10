package com.stock.service;

import org.springframework.stereotype.Service;

@Service
public class PriceUpdateService {

    private final StockService stockService;

    public PriceUpdateService(StockService stockService) {
        this.stockService = stockService;
    }

    // Update prices for all stocks from market data service
    // Delegates to StockService.updateAllStockPrices() to ensure core logic reuse
    public void updateAllPrices() {
        stockService.updateAllStockPrices();
    }
}