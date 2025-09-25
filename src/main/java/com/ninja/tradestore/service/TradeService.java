package com.ninja.tradestore.service;


import com.ninja.tradestore.entity.Trade;
import com.ninja.tradestore.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Transactional
    public Trade saveTrade(Trade incomingTrade) {
        return tradeRepository.findTopByTradeIdOrderByVersionDesc(incomingTrade.getTradeId())
                .map(currentTrade -> {
                    if (incomingTrade.getVersion() < currentTrade.getVersion()) {
                        throw new IllegalArgumentException(
                                "Rejecting trade: version " + incomingTrade.getVersion() +
                                        " is lower than current version " + currentTrade.getVersion()
                        );
                    }
                    return tradeRepository.save(incomingTrade);
                })
                .orElseGet(() -> tradeRepository.save(incomingTrade));
    }
}
