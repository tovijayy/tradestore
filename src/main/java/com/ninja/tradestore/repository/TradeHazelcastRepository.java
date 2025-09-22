package com.ninja.tradestore.repository;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import com.ninja.tradestore.exceptions.TradeNotFoundException;
import com.ninja.tradestore.model.TradeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class TradeHazelcastRepository {

    private final String mapName = "trades";
    private final IMap<String, TradeDocument> tradeMap;
    private static final Logger logger = LoggerFactory.getLogger(TradeHazelcastRepository.class);


    @Autowired
    public TradeHazelcastRepository(HazelcastInstance hazelcastInstance, @Value("${tradestore.hazelcast.map.name:trades}") String mapName) {
        this.tradeMap = hazelcastInstance.getMap(mapName);

    }

    public TradeDocument save(TradeDocument tradeDocument) {
        validateTradeDocument(tradeDocument);
        String key = tradeDocument.getCompositeKey();

        try {
            tradeMap.put(key, tradeDocument);
            return tradeDocument;
        } catch (Exception e) {
            logger.error("Failed to save trade with key: {}", key, e);
            throw new RuntimeException("Failed to save trade document", e);
        }
    }

    public boolean existsByTradeIdAndVersion(String tradeId, Integer version) {
        validateTradeIdAndVersion(tradeId, version);
        String key = buildCompositeKey(tradeId, version);

        try{
            boolean exists = tradeMap.containsKey(key);
            logger.debug("Trade existence check for key {}: {}", key, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Failed to check existence for key: {}", key, e);
            throw new RuntimeException("Failed to check trade existence", e);
        }


    }

    public List<TradeDocument> findById(String tradeId) {
        try {
            Collection<TradeDocument> trades = tradeMap.values(
                    Predicates.equal("tradeId", tradeId)
            );
            logger.debug("Found {} trades for trade ID: {}", trades.size(), tradeId);
            return List.copyOf(trades);
        } catch (Exception e) {
            logger.error("Failed to find trades by trade ID: {}", tradeId, e);
            throw new TradeNotFoundException("Failed to find trades by trade ID", e);
        }

    }

    public Optional<TradeDocument> findByCompositeKey(String compositeKey) {

        try {
            TradeDocument trade = tradeMap.get(compositeKey);
            logger.debug("Trade found for key {}: {}", compositeKey, trade != null);
            return Optional.ofNullable(trade);
        } catch (Exception e) {
            logger.error("Failed to find trade by composite key: {}", compositeKey, e);
            throw new RuntimeException("Failed to find trade by composite key", e);
        }
    }

    public List<TradeDocument> findAll() {
        try {
            Collection<TradeDocument> trades = tradeMap.values();
            logger.debug("Found {} total trades", trades.size());
            return List.copyOf(trades);
        } catch (Exception e) {
            logger.error("Failed to find all trades", e);
            throw new TradeNotFoundException("Failed to retrieve all trades", e);
        }
    }

    // Private helper methods

    private void validateTradeDocument(TradeDocument tradeDocument) {
        if (tradeDocument == null) {
            throw new IllegalArgumentException("Trade document cannot be null");
        }

        String compositeKey = tradeDocument.getCompositeKey();
        if (compositeKey == null || compositeKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Trade document must have a valid composite key");
        }
    }

    private void validateTradeIdAndVersion(String tradeId, Integer version) {
        validateTradeId(tradeId);
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
    }

    private void validateTradeId(String tradeId) {
        if (tradeId == null || tradeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Trade ID cannot be null or empty");
        }
    }

    private String buildCompositeKey(String tradeId, Integer version) {
        return tradeId + "_" + version;
    }

}
