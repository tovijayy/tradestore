package com.ninja.tradestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.entity.Trade;
import com.ninja.tradestore.repository.TradeHazelcastRepository;
import com.ninja.tradestore.repository.TradeJpaRepository;
import com.ninja.tradestore.repository.TradeRepository;
import com.ninja.tradestore.service.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    TradeService tradeService;

    @Autowired
    TradeRepository tradeRepository;

    @PostMapping
    public ResponseEntity<String> sendTrade(@RequestBody Trade trade) {
        try {
            String tradeJson = objectMapper.writeValueAsString(trade);
            tradeService.saveTrade(trade);
            return new ResponseEntity<>(tradeJson, org.springframework.http.HttpStatus.OK);
        } catch (JsonProcessingException e) {
            logger.error("Unable to process request", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }
}
