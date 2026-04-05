package com.external.finnhub;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FinnhubClientImpl implements FinnhubClient {

    private static final Logger log = LoggerFactory.getLogger(FinnhubClientImpl.class);
    private static final String FINNHUB_BASE_URL = "https://finnhub.io/api/v1";

    private final String apiKey;
    private final RestTemplate restTemplate;

    public FinnhubClientImpl(RestTemplate restTemplate, @Value("${finnhub.api.key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Override
    public Optional<Double> getQuote(String symbol) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Finnhub API key is not configured; skipping quote fetch for {}", symbol);
            return Optional.empty();
        }

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
                return Optional.empty();
            }

            return Optional.of(response.getC());
        } catch (Exception e) {
            log.error("Failed to fetch quote for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Map<String, Double> getQuotes(String symbols) {
        Map<String, Double> result = new HashMap<>();
        String[] symbolList = symbols.split(",");

        for (int i = 0; i < symbolList.length; i++) {
            String symbol = symbolList[i];
            String trimmedSymbol = symbol.trim();
            if (trimmedSymbol.isEmpty()) {
                continue;
            }

            Optional<Double> price = getQuote(trimmedSymbol);
            if (price.isPresent()) {
                result.put(trimmedSymbol.toUpperCase(), price.get());
            } else {
                log.warn("Failed to get price for symbol: {}", trimmedSymbol);
            }

            if (i < symbolList.length - 1) {
                try {
                    sleepBetweenRequests();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Rate limiting sleep interrupted");
                    break;
                }
            }
        }

        return result;
    }

    protected void sleepBetweenRequests() throws InterruptedException {
        Thread.sleep(1100);
    }

    static class FinnhubQuoteResponse {
        private Double c;
        private Double d;
        private Double dp;
        private Double h;
        private Double l;
        private Double o;
        private Double pc;
        private Long t;

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
