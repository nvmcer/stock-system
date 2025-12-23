package com.stock.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.dto.StockRequestDto;
import com.stock.dto.StockResponseDto;
import com.stock.entity.Stock;
import com.stock.repository.StockRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    public List<StockResponseDto> findAll() { 
      return stockRepository.findAll()
        .stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());
    }

    public StockResponseDto findById(Long id) { 
      Stock stock = stockRepository.findById(id).
        orElseThrow(() -> new EntityNotFoundException("Stock not found: " + id));
      return toResponseDto(stock); 
    }

    public StockResponseDto findBySymbol(String symbol) {
      Stock stock = stockRepository.findBySymbol(symbol)
              .orElseThrow(() -> new EntityNotFoundException("Stock not found: " + symbol));
      return toResponseDto(stock);
    }
    
    public StockResponseDto create(StockRequestDto request) {
      Stock stock = toEntity(request);
      Stock saved = stockRepository.save(stock);
      return toResponseDto(saved);
    }

    public StockResponseDto update(Long id, StockRequestDto request) {
      Stock stock = stockRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException("Stock not found: " + id));

      stock.setSymbol(request.getSymbol());
      stock.setName(request.getName());
      stock.setPrice(request.getPrice());

      return toResponseDto(stockRepository.save(stock));
    }

    public void delete(Long id) {
      if (!stockRepository.existsById(id)) {
        throw new EntityNotFoundException("Stock not found: " + id);
      }
      stockRepository.deleteById(id); 
    }

    private Stock toEntity(StockRequestDto dto) {
      Stock stock = new Stock();
      stock.setSymbol(dto.getSymbol());
      stock.setName(dto.getName());
      stock.setPrice(dto.getPrice());
      return stock;
    }

    private StockResponseDto toResponseDto(Stock stock) {
      StockResponseDto dto = new StockResponseDto();
      dto.setId(stock.getId());
      dto.setSymbol(stock.getSymbol());
      dto.setName(stock.getName());
      dto.setPrice(stock.getPrice());
      return dto;
    }

}
