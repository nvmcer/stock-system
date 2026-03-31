package com.external.marketdata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.external.finnhub.FinnhubClient;

@ExtendWith(MockitoExtension.class)
class MarketDataClientImplTest {

    @Mock
    private FinnhubClient finnhubClient;

    @InjectMocks
    private MarketDataClientImpl marketDataClient;

    @Test
    void getPrices_shouldReturnPrices_whenFinnhubClientReturnsPrices() {
        Map<String, Double> expected = Map.of("AAPL", 150.25, "GOOGL", 2750.50);
        when(finnhubClient.getQuotes(anyString())).thenReturn(expected);

        Map<String, Double> result = marketDataClient.getPrices("AAPL,GOOGL");

        assertEquals(expected, result);
    }

    @Test
    void getPrices_shouldReturnEmptyMap_whenFinnhubClientReturnsEmpty() {
        when(finnhubClient.getQuotes(anyString())).thenReturn(Map.of());

        Map<String, Double> result = marketDataClient.getPrices("AAPL,GOOGL");

        assertTrue(result.isEmpty());
    }

    @Test
    void getPrices_shouldReturnEmptyMap_whenFinnhubClientThrowsException() {
        when(finnhubClient.getQuotes(anyString())).thenThrow(new RuntimeException("API error"));

        Map<String, Double> result = marketDataClient.getPrices("AAPL,GOOGL");

        assertTrue(result.isEmpty());
    }
}