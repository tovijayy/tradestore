package com.ninja.tradestore.listener;



import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.service.SQLTradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradeMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(TradeMessageListener.class);

    @Autowired
    private SQLTradeService tradeService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receiveMessage(String message) {
        try {
            SqlTrade trade = objectMapper.readValue(message, SqlTrade.class);
            logger.info("Received Trade message: {}", trade);
            tradeService.processTrade(trade);
        } catch (Exception e) {
            logger.error("Error processing trade message: {}", message, e);
            // Here you might want to send the message to a dead-letter queue
        }
    }
}
