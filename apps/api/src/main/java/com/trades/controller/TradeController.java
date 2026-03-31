package com.trades.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exception.ApiResponse;
import com.trades.dto.TradeRequestDto;
import com.trades.dto.TradeResponseDto;
import com.trades.entity.Trade;
import com.trades.service.TradeService;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<Trade>> buy(@RequestParam Long userId, @RequestBody TradeRequestDto request) {
        request.setType("BUY");
        Trade trade = tradeService.executeTrade(userId, request);
        return ResponseEntity.ok(ApiResponse.success(trade, "Buy order executed successfully"));
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<Trade>> sell(@RequestParam Long userId, @RequestBody TradeRequestDto request) {
        request.setType("SELL");
        Trade trade = tradeService.executeTrade(userId, request);
        return ResponseEntity.ok(ApiResponse.success(trade, "Sell order executed successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TradeResponseDto>>> getTradeHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(tradeService.getTradeHistory(userId), "Trade history retrieved successfully"));
    }
}
