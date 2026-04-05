package com.external.finnhub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class FinnhubClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    private FinnhubClientImpl finnhubClient;

    @BeforeEach
    void setUp() {
        finnhubClient = new FinnhubClientImpl(restTemplate, "test-api-key");
    }

    @Test
    void getQuote_shouldReturnPrice_whenResponseIsValid() {
        FinnhubClientImpl.FinnhubQuoteResponse response = new FinnhubClientImpl.FinnhubQuoteResponse();
        response.setC(150.25);

        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(response);

        Optional<Double> price = finnhubClient.getQuote("AAPL");

        assertTrue(price.isPresent());
        assertEquals(150.25, price.get());
    }

    @Test
    void getQuote_shouldReturnEmpty_whenResponseIsNull() {
        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(null);

        Optional<Double> price = finnhubClient.getQuote("AAPL");

        assertTrue(price.isEmpty());
    }

    @Test
    void getQuote_shouldReturnEmpty_whenCurrentPriceIsNull() {
        FinnhubClientImpl.FinnhubQuoteResponse response = new FinnhubClientImpl.FinnhubQuoteResponse();
        response.setC(null);

        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(response);

        Optional<Double> price = finnhubClient.getQuote("AAPL");

        assertTrue(price.isEmpty());
    }

    @Test
    void getQuote_shouldReturnEmpty_whenCurrentPriceIsZero() {
        FinnhubClientImpl.FinnhubQuoteResponse response = new FinnhubClientImpl.FinnhubQuoteResponse();
        response.setC(0.0);

        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(response);

        Optional<Double> price = finnhubClient.getQuote("AAPL");

        assertTrue(price.isEmpty());
    }

    @Test
    void getQuote_shouldReturnEmpty_whenExceptionThrown() {
        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenThrow(new RuntimeException("Network error"));

        Optional<Double> price = finnhubClient.getQuote("AAPL");

        assertTrue(price.isEmpty());
    }

    @Test
    void getQuotes_shouldReturnMapOfPrices_forMultipleSymbols() throws Exception {
        FinnhubClientImpl spyClient = spy(finnhubClient);
        doNothing().when(spyClient).sleepBetweenRequests();

        FinnhubClientImpl.FinnhubQuoteResponse response1 = new FinnhubClientImpl.FinnhubQuoteResponse();
        response1.setC(150.25);
        FinnhubClientImpl.FinnhubQuoteResponse response2 = new FinnhubClientImpl.FinnhubQuoteResponse();
        response2.setC(2750.50);

        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(response1)
                .thenReturn(response2);

        var result = spyClient.getQuotes("AAPL,GOOGL");

        assertEquals(2, result.size());
        assertEquals(150.25, result.get("AAPL"));
        assertEquals(2750.50, result.get("GOOGL"));
    }

    @Test
    void getQuotes_shouldSkipEmptySymbols() throws Exception {
        FinnhubClientImpl spyClient = spy(finnhubClient);
        doNothing().when(spyClient).sleepBetweenRequests();

        FinnhubClientImpl.FinnhubQuoteResponse response = new FinnhubClientImpl.FinnhubQuoteResponse();
        response.setC(150.25);

        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(response)
                .thenReturn(response);

        var result = spyClient.getQuotes("AAPL, ,GOOGL");

        assertEquals(2, result.size());
        assertEquals(150.25, result.get("AAPL"));
        assertEquals(150.25, result.get("GOOGL"));
    }

    @Test
    void getQuotes_shouldHandleFailedSymbols() throws Exception {
        FinnhubClientImpl spyClient = spy(finnhubClient);
        doNothing().when(spyClient).sleepBetweenRequests();

        FinnhubClientImpl.FinnhubQuoteResponse response1 = new FinnhubClientImpl.FinnhubQuoteResponse();
        response1.setC(150.25);

        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(response1)
                .thenReturn(null);

        var result = spyClient.getQuotes("AAPL,GOOGL");

        assertEquals(1, result.size());
        assertEquals(150.25, result.get("AAPL"));
    }

    @Test
    void getQuotes_shouldReturnEmptyMap_whenAllSymbolsFail() throws Exception {
        FinnhubClientImpl spyClient = spy(finnhubClient);
        doNothing().when(spyClient).sleepBetweenRequests();

        when(restTemplate.getForObject(anyString(), eq(FinnhubClientImpl.FinnhubQuoteResponse.class)))
                .thenReturn(null);

        var result = spyClient.getQuotes("AAPL,GOOGL");

        assertEquals(0, result.size());
    }
}
