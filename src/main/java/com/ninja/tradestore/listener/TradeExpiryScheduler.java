package com.ninja.tradestore.listener;

import com.ninja.tradestore.service.SQLTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class TradeExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(TradeExpiryScheduler.class);
    private final SQLTradeService tradeService;

    public TradeExpiryScheduler(SQLTradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // e.g., "0 0 1 * *?"
    public void markExpiredTrades() {
        log.info("Starting trade expiration job...");
        tradeService.expireTrades();
        log.info("Trade expiration job finished.");
    }


}
