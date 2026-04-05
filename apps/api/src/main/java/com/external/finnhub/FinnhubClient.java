package com.external.finnhub;

import java.util.Map;
import java.util.Optional;

public interface FinnhubClient {
    Optional<Double> getQuote(String symbol);
    Map<String, Double> getQuotes(String symbols);
}
