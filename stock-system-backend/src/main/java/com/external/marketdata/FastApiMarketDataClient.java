package com.external.marketdata;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("dev")
public class FastApiMarketDataClient implements MarketDataClient {

    @Value("${fastapi.url}")
    private String fastApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, Double> getPrices(String symbols) {
        String url = fastApiUrl + "/prices?symbols=" + symbols;
        return restTemplate.getForObject(url, Map.class);
    }
}

