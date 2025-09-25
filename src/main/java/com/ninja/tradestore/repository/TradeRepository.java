package com.ninja.tradestore.repository;

import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



    @Repository
    public interface TradeRepository extends JpaRepository<Trade, String> {
        Optional<Trade> findTopByTradeIdOrderByVersionDesc(String tradeId);
    }


