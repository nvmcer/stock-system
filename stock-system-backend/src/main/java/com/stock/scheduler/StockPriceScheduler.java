package com.stock.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stock.service.StockService;

@Component
public class StockPriceScheduler {
    private static final Logger log = LoggerFactory.getLogger(StockPriceScheduler.class);

    private final StockService stockService;

    @Autowired
    public StockPriceScheduler(StockService stockService) {
        this.stockService = stockService;
    }

    // Scheduled to run at 5:00 PM every Monday through Friday in America/New_York timezone
    // This corresponds to after US stock market close (NYSE/NASDAQ close at 4:00 PM ET)
    @Scheduled(cron = "0 0 17 * * MON-FRI", zone = "America/New_York")
    public void scheduledPriceUpdate() {
        log.info("Starting scheduled stock price update after market close");
        try {
            stockService.updateAllStockPrices();
            log.info("Scheduled stock price update completed successfully");
        } catch (Exception e) {
            log.error("Scheduled stock price update failed", e);
        }
    }
}