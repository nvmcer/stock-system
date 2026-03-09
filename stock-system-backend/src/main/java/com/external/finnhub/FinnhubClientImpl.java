package com.external.finnhub;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FinnhubClientImpl implements FinnhubClient {

    private static final Logger log = LoggerFactory.getLogger(FinnhubClientImpl.class);
    private static final String FINNHUB_BASE_URL = "https://finnhub.io/api/v1";

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public FinnhubClientImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public Double getQuote(String symbol) {
        try {
            String url = UriComponentsBuilder.fromUriString(FINNHUB_BASE_URL)
                    .path("/quote")
                    .queryParam("symbol", symbol.toUpperCase())
                    .queryParam("token", apiKey)
                    .build()
                    .toUriString();

            FinnhubQuoteResponse response = restTemplate.getForObject(url, FinnhubQuoteResponse.class);

            if (response == null || response.getC() == null || response.getC() == 0) {
                log.warn("Invalid quote data for symbol: {}", symbol);
                return null;
            }

            return response.getC();
        } catch (Exception e) {
            log.error("Failed to fetch quote for {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, Double> getQuotes(String symbols) {
        Map<String, Double> result = new HashMap<>();
        String[] symbolList = symbols.split(",");

        for (String symbol : symbolList) {
            String trimmedSymbol = symbol.trim();
            if (trimmedSymbol.isEmpty()) {
                continue;
            }

            Double price = getQuote(trimmedSymbol);
            if (price != null) {
                result.put(trimmedSymbol.toUpperCase(), price);
            } else {
                log.warn("Failed to get price for symbol: {}", trimmedSymbol);
            }
        }

        return result;
    }

    private static class FinnhubQuoteResponse {
        private Double c;  // Current price
        private Double d;  // Change
        private Double dp; // Percent change
        private Double h;  // High price of the day
        private Double l;  // Low price of the day
        private Double o;  // Open price of the day
        private Double pc; // Previous close price
        private Long t;    // Timestamp

        public Double getC() {
            return c;
        }

        public void setC(Double c) {
            this.c = c;
        }

        public Double getD() {
            return d;
        }

        public void setD(Double d) {
            this.d = d;
        }

        public Double getDp() {
            return dp;
        }

        public void setDp(Double dp) {
            this.dp = dp;
        }

        public Double getH() {
            return h;
        }

        public void setH(Double h) {
            this.h = h;
        }

        public Double getL() {
            return l;
        }

        public void setL(Double l) {
            this.l = l;
        }

        public Double getO() {
            return o;
        }

        public void setO(Double o) {
            this.o = o;
        }

        public Double getPc() {
            return pc;
        }

        public void setPc(Double pc) {
            this.pc = pc;
        }

        public Long getT() {
            return t;
        }

        public void setT(Long t) {
            this.t = t;
        }
    }
}
