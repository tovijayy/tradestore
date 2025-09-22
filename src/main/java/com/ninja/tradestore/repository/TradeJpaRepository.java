package com.ninja.tradestore.repository;



import com.ninja.tradestore.entity.SqlTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TradeJpaRepository extends JpaRepository<SqlTrade, String> {
    Optional<SqlTrade> findTopByTradeIdOrderByVersionDesc(String tradeId);
    List<SqlTrade> findByMaturityDateBefore(LocalDate date);
}
