package com.external.marketdata;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class MarketDataClientImpl implements MarketDataClient {
    @Value("${market.data.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public MarketDataClientImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .requestFactory(() -> {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
                    factory.setReadTimeout((int) Duration.ofSeconds(30).toMillis());
                    return factory;
                })
                .build();
    }
    
    @Override
    public Map<String, Double> getPrices(String symbols) {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl.replaceAll("/$", ""))
                    .path("/prices")
                    .queryParam("symbols", symbols)
                    .build()
                    .toUriString();

            ParameterizedTypeReference<Map<String, Double>> typeRef = 
                new ParameterizedTypeReference<>() {};
            
            ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, typeRef);

            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch prices from market data service", e);
            return Collections.emptyMap();
        }
    }
}
