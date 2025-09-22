package com.ninja.tradestore.service;



import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.model.TradeDocument;
import com.ninja.tradestore.repository.TradeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SQLTradeService {

    private final TradeJpaRepository tradeRepository;
    private final TradeHazelcastService tradeHazelcastService;

    public SQLTradeService(TradeJpaRepository tradeRepository, TradeHazelcastService tradeHazelcastService) {
        this.tradeRepository = tradeRepository;
        this.tradeHazelcastService = tradeHazelcastService;
    }

    public void processTrade(SqlTrade trade) {
        if (isMaturityDateValid(trade)) { // Reject if maturity date < today
            Optional<SqlTrade> existingTradeOpt = tradeRepository.findTopByTradeIdOrderByVersionDesc(trade.getTradeId());
            //check if existing trade version
            if (existingTradeOpt.isPresent()) {
                SqlTrade existingTrade = existingTradeOpt.get();
                if (trade.getVersion() < existingTrade.getVersion()) {
                    throw new RuntimeException("Received trade version is lower than the existing version for Trade ID: " + trade.getTradeId());
                }
            }
            trade.setCreatedDate(LocalDate.now());
            trade.setExpired('N');
            tradeRepository.save(trade);
            tradeHazelcastService.saveOrUpdateTrade(trade);
        } else {
            //In case of maturity < today - this should purged or move to archived data base for analytics
            throw new RuntimeException("Maturity date is earlier than today's date for Trade ID: " + trade.getTradeId());
        }
    }

    private boolean isMaturityDateValid(SqlTrade trade) {
        return !trade.getMaturityDate().isBefore(LocalDate.now());
    }

    public void expireTrades() {
        tradeRepository.findByMaturityDateBefore(LocalDate.now()).stream()
                .filter(trade -> trade.getExpired() == 'N')
                .forEach(trade -> {
                    trade.setExpired('Y');
                    tradeRepository.save(trade);
                    String key = buildCompositeKey(trade.getTradeId(), trade.getVersion());
                    // If the expired trade is the latest version, update it in Hazelcast too
                    Optional<TradeDocument> latestInMapOpt = tradeHazelcastService.findByCompositeKey(key);
                    if (latestInMapOpt.isPresent()) {
                        TradeDocument latestInMap = latestInMapOpt.get();
                        if (latestInMap.getVersion() == trade.getVersion()) {
                            tradeHazelcastService.saveOrUpdateTrade(trade);
                        }
                    }
                });
    }


    //private helper methods

    private String buildCompositeKey(String tradeId, Integer version) {
        return tradeId + "_" + version;
    }

}
