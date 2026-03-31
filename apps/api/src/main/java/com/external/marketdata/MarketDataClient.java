package com.external.marketdata;

import java.util.Map;

public interface MarketDataClient {
    Map<String, Double> getPrices(String symbols);
}

