package com.trades.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exception.ApiResponse;
import com.security.CurrentUserService;
import com.trades.dto.TradeRequestDto;
import com.trades.dto.TradeResponseDto;
import com.trades.entity.Trade;
import com.trades.service.TradeService;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    private final TradeService tradeService;
    private final CurrentUserService currentUserService;

    public TradeController(TradeService tradeService, CurrentUserService currentUserService) {
        this.tradeService = tradeService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<Trade>> buy(
            @RequestParam(required = false) Long userId,
            @RequestBody TradeRequestDto request,
            Authentication authentication) {
        Long resolvedUserId = currentUserService.resolveUserId(authentication, userId);
        request.setType("BUY");
        Trade trade = tradeService.executeTrade(resolvedUserId, request);
        return ResponseEntity.ok(ApiResponse.success(trade, "Buy order executed successfully"));
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<Trade>> sell(
            @RequestParam(required = false) Long userId,
            @RequestBody TradeRequestDto request,
            Authentication authentication) {
        Long resolvedUserId = currentUserService.resolveUserId(authentication, userId);
        request.setType("SELL");
        Trade trade = tradeService.executeTrade(resolvedUserId, request);
        return ResponseEntity.ok(ApiResponse.success(trade, "Sell order executed successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TradeResponseDto>>> getTradeHistory(
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        Long resolvedUserId = currentUserService.resolveUserId(authentication, userId);
        return ResponseEntity.ok(ApiResponse.success(tradeService.getTradeHistory(resolvedUserId), "Trade history retrieved successfully"));
    }
}
