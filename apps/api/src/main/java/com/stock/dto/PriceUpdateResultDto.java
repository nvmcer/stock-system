package com.stock.dto;

import java.util.List;

public class PriceUpdateResultDto {
    private int totalStocks;
    private int updatedCount;
    private int failedCount;
    private List<String> failedSymbols;

    public int getTotalStocks() {
        return totalStocks;
    }

    public void setTotalStocks(int totalStocks) {
        this.totalStocks = totalStocks;
    }

    public int getUpdatedCount() {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount) {
        this.updatedCount = updatedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public List<String> getFailedSymbols() {
        return failedSymbols;
    }

    public void setFailedSymbols(List<String> failedSymbols) {
        this.failedSymbols = failedSymbols;
    }

    public boolean hasAnyUpdates() {
        return updatedCount > 0;
    }
}
