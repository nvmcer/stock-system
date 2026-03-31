package com.external.marketdata;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.external.finnhub.FinnhubClient;

@Component
public class MarketDataClientImpl implements MarketDataClient {

    private static final Logger log = LoggerFactory.getLogger(MarketDataClientImpl.class);

    private final FinnhubClient finnhubClient;

    public MarketDataClientImpl(FinnhubClient finnhubClient) {
        this.finnhubClient = finnhubClient;
    }

    @Override
    public Map<String, Double> getPrices(String symbols) {
        try {
            log.info("Fetching prices for symbols: {}", symbols);
            Map<String, Double> prices = finnhubClient.getQuotes(symbols);
            log.info("Fetched {} prices", prices.size());
            return prices;
        } catch (Exception e) {
            log.error("Failed to fetch prices from Finnhub: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}
