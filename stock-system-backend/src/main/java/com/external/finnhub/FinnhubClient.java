package com.external.finnhub;

import java.util.Map;

public interface FinnhubClient {
    Double getQuote(String symbol);
    Map<String, Double> getQuotes(String symbols);
}
