package com.ninja.tradestore.service;

import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.model.TradeDocument;
import com.ninja.tradestore.repository.TradeHazelcastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TradeHazelcastService {

    private final TradeHazelcastRepository tradeHazelcastRepository;

    @Autowired
    public TradeHazelcastService(TradeHazelcastRepository tradeHazelcastRepository) {
        this.tradeHazelcastRepository = tradeHazelcastRepository;
    }

    public TradeDocument createTradeDocument(TradeDocument tradeDocument) {
        return tradeHazelcastRepository.save(tradeDocument);
    }

    /**
     * Saves or updates a trade in Hazelcast by converting the JPA entity.
     * @param trade The Trade entity from the main service.
     */
    public void saveOrUpdateTrade(SqlTrade trade) {
        TradeDocument tradeDocument = new TradeDocument(
                trade.getTradeId(),
                trade.getVersion(),
                trade.getCounterPartyId(),
                trade.getBookId(),
                trade.getMaturityDate(),
                trade.getCreatedDate(),
                trade.getExpired()
        );
        tradeHazelcastRepository.save(tradeDocument);
    }

    public Optional<TradeDocument> findByCompositeKey(String compositeKey) {
        return tradeHazelcastRepository.findByCompositeKey(compositeKey);
    }

    public List<TradeDocument> getAllTrades() {
        return tradeHazelcastRepository.findAll();
    }

    public boolean tradeDocumentExists(String tradeId, Integer version) {
        return tradeHazelcastRepository.existsByTradeIdAndVersion(tradeId, version);
    }
}
