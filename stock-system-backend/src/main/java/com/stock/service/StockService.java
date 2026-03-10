package com.stock.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.external.marketdata.MarketDataClient;
import com.stock.dto.StockRequestDto;
import com.stock.dto.StockResponseDto;
import com.stock.entity.Stock;
import com.stock.repository.StockRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class StockService {
    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;
    private final MarketDataClient marketDataClient;

    public StockService(StockRepository stockRepository, MarketDataClient marketDataClient) {
        this.stockRepository = stockRepository;
        this.marketDataClient = marketDataClient;
    }

    // Retrieve all stocks from database
    public List<StockResponseDto> findAll() { 
      return stockRepository.findAll()
        .stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());
    }

    // Find stock by ID
    public StockResponseDto findById(Long id) { 
      Stock stock = stockRepository.findById(id).
        orElseThrow(() -> new EntityNotFoundException("Stock not found: " + id));
      return toResponseDto(stock); 
    }

    // Find stock by ticker symbol
    public StockResponseDto findBySymbol(String symbol) {
      Stock stock = stockRepository.findBySymbol(symbol)
              .orElseThrow(() -> new EntityNotFoundException("Stock not found: " + symbol));
      return toResponseDto(stock);
    }
    
    // Create a new stock
    public StockResponseDto create(StockRequestDto request) {
      Stock stock = toEntity(request);
      Stock saved = stockRepository.save(stock);
      return toResponseDto(saved);
    }

    // Update existing stock
    public StockResponseDto update(Long id, StockRequestDto request) {
      Stock stock = stockRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException("Stock not found: " + id));

      stock.setSymbol(request.getSymbol());
      stock.setName(request.getName());
      stock.setPrice(request.getPrice());

      return toResponseDto(stockRepository.save(stock));
    }

    // Delete stock by ID
    public void delete(Long id) {
      if (!stockRepository.existsById(id)) {
        throw new EntityNotFoundException("Stock not found: " + id);
      }
      stockRepository.deleteById(id); 
    }

    // Update prices for all stocks from market data service
    public void updateAllStockPrices() {
        log.info("Starting stock price update for all stocks");
        
        // Get all stocks from database
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            log.info("No stocks found in database, skipping price update");
            return;
        }

        // Prepare comma-separated symbol list for API call
        String symbols = stocks.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.joining(","));
        log.debug("Fetching prices for symbols: {}", symbols);

        // Fetch latest prices from Finnhub API (rate limiting handled in client)
        Map<String, Double> prices = marketDataClient.getPrices(symbols);
        log.debug("Received prices for {} symbols", prices.size());

        int successCount = 0;
        int failCount = 0;

        // Update each stock with latest price
        for (Stock stock : stocks) {
            try {
                Double newPrice = prices.get(stock.getSymbol());

                if (newPrice == null) {
                    throw new RuntimeException("Price missing for symbol: " + stock.getSymbol());
                }

                stock.setPrice(BigDecimal.valueOf(newPrice));
                successCount++;

            } catch (RuntimeException e) {
                failCount++;
                log.warn("Failed to update price for symbol {}: {}", stock.getSymbol(), e.getMessage());
            }
        }

        // Save all updated stocks to database
        List<Stock> updatedStocks = stocks.stream()
                .filter(s -> s.getPrice() != null)
                .collect(Collectors.toList());

        stockRepository.saveAll(updatedStocks);
        log.info("Stock price update completed: {} succeeded, {} failed", successCount, failCount);
    }

    // Convert DTO to entity
    private Stock toEntity(StockRequestDto dto) {
      Stock stock = new Stock();
      stock.setSymbol(dto.getSymbol());
      stock.setName(dto.getName());
      stock.setPrice(dto.getPrice());
      return stock;
    }

    // Convert entity to DTO for API response
    private StockResponseDto toResponseDto(Stock stock) {
      StockResponseDto dto = new StockResponseDto();
      dto.setId(stock.getId());
      dto.setSymbol(stock.getSymbol());
      dto.setName(stock.getName());
      dto.setPrice(stock.getPrice());
      return dto;
    }

}