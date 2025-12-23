package com.stock.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.dto.StockRequestDto;
import com.stock.dto.StockResponseDto;
import com.stock.service.PriceUpdateService;
import com.stock.service.StockService;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;
    private final PriceUpdateService priceUpdateService;

    @Autowired
    public StockController(StockService stockService, PriceUpdateService priceUpdateService) {
        this.stockService = stockService;
        this.priceUpdateService = priceUpdateService;
    }

    @GetMapping
    public List<StockResponseDto> getAll() {
        return stockService.findAll();
    }

    @GetMapping("/{id}")
    public StockResponseDto getStockById(@PathVariable Long id) {
        return stockService.findById(id);
    }

    @GetMapping("/symbol/{symbol}")
    public StockResponseDto getStockBySymbol(@PathVariable String symbol) {
        return stockService.findBySymbol(symbol);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public StockResponseDto create(@RequestBody StockRequestDto request) {
        return stockService.create(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public StockResponseDto updateStock(@PathVariable Long id, @RequestBody StockRequestDto request) {
        return stockService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        stockService.delete(id);
    }
    
    @PostMapping("/update-prices")
    public ResponseEntity<?> updatePrices() {
        priceUpdateService.updateAllPrices();
        return ResponseEntity.ok(Map.of("message", "Update Stock Prices Successful"));
    }
}
