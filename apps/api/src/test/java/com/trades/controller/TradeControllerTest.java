package com.trades.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.trades.dto.TradeRequestDto;
import com.trades.dto.TradeResponseDto;
import com.trades.entity.Trade;
import com.trades.service.TradeService;
import com.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

    @Mock
    private TradeService tradeService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Authentication authentication;

    private TradeController tradeController;

    @BeforeEach
    void setUp() {
        tradeController = new TradeController(tradeService, currentUserService);
    }

    @Test
    void getTradeHistory_shouldUseResolvedUserId() {
        when(currentUserService.resolveUserId(authentication, 999L)).thenReturn(1L);
        when(tradeService.getTradeHistory(1L)).thenReturn(List.of(new TradeResponseDto()));

        var response = tradeController.getTradeHistory(999L, authentication);

        assertEquals(200, response.getStatusCode().value());
        verify(tradeService).getTradeHistory(1L);
    }

    @Test
    void buy_shouldUseResolvedUserIdAndForceBuyType() {
        TradeRequestDto request = new TradeRequestDto();
        request.setSymbol("AAPL");
        request.setQuantity(2);
        request.setPrice(BigDecimal.valueOf(123.45));

        Trade trade = new Trade();
        when(currentUserService.resolveUserId(authentication, 999L)).thenReturn(999L);
        when(tradeService.executeTrade(eq(999L), any(TradeRequestDto.class))).thenReturn(trade);

        var response = tradeController.buy(999L, request, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("BUY", request.getType());
        verify(tradeService).executeTrade(eq(999L), any(TradeRequestDto.class));
    }
}
