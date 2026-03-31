package com.stock.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public class StockRequestDto {

    @NotBlank
    private String symbol;

    @NotBlank
    private String name;

    @NotBlank
    private BigDecimal price;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
