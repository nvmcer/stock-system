package com.stock.controller;

import java.util.List;

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

import com.exception.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<StockResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(stockService.findAll(), "Stocks retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponseDto>> getStockById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(stockService.findById(id), "Stock retrieved successfully"));
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<ApiResponse<StockResponseDto>> getStockBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(ApiResponse.success(stockService.findBySymbol(symbol), "Stock retrieved successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<StockResponseDto>> create(@RequestBody StockRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(stockService.create(request), "Stock created successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockResponseDto>> updateStock(@PathVariable Long id, @RequestBody StockRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(stockService.update(id, request), "Stock updated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        stockService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Stock deleted successfully"));
    }
    
    @PostMapping("/update-prices")
    public ResponseEntity<ApiResponse<String>> updatePrices() {
        priceUpdateService.updateAllPrices();
        return ResponseEntity.ok(ApiResponse.success("Update Stock Prices Successful", "Prices updated successfully"));
    }
}
