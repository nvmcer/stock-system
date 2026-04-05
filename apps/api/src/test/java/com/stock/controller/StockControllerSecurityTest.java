package com.stock.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import com.stock.dto.PriceUpdateResultDto;
import com.stock.service.PriceUpdateService;

@ExtendWith(MockitoExtension.class)
class StockControllerSecurityTest {

    @Mock
    private PriceUpdateService priceUpdateService;

    private StockController stockController;

    @BeforeEach
    void setUp() {
        stockController = new StockController(null, priceUpdateService);
    }

    @Test
    void updatePrices_shouldRequireAdminRole() throws Exception {
        Method method = StockController.class.getMethod("updatePrices");
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasRole('ADMIN')", annotation.value());
    }

    @Test
    void updatePrices_shouldReturnSuccessResponseWhenPricesUpdate() {
        PriceUpdateResultDto result = createResult(1, 1, 0, List.of());
        when(priceUpdateService.updateAllPrices()).thenReturn(result);

        var response = stockController.updatePrices();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void updatePrices_shouldReturnBadRequestWhenNothingUpdated() {
        PriceUpdateResultDto result = createResult(1, 0, 1, List.of("TSLA"));
        when(priceUpdateService.updateAllPrices()).thenReturn(result);

        var response = stockController.updatePrices();

        assertEquals(400, response.getStatusCode().value());
        assertEquals(false, response.getBody().isSuccess());
    }

    private static PriceUpdateResultDto createResult(int total, int updated, int failed, List<String> failedSymbols) {
        PriceUpdateResultDto result = new PriceUpdateResultDto();
        result.setTotalStocks(total);
        result.setUpdatedCount(updated);
        result.setFailedCount(failed);
        result.setFailedSymbols(failedSymbols);
        return result;
    }
}
