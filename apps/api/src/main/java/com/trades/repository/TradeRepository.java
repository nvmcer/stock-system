package com.trades.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trades.entity.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUserId(Long userId);

    Optional<Trade> findTopByStockIdOrderByTimestampDesc(Long stockId);
}