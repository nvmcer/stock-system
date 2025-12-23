package com.trades.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Trade buy(@RequestParam Long userId, @RequestBody TradeRequestDto request) {
        request.setType("BUY");
        return tradeService.executeTrade(userId, request);
    }

    @PostMapping("/sell")
    public Trade sell(@RequestParam Long userId, @RequestBody TradeRequestDto request) {
        request.setType("SELL");
        return tradeService.executeTrade(userId, request);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TradeResponseDto>> getTradeHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(tradeService.getTradeHistory(userId));
    }
}