package com.ninja.tradestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.repository.TradeJpaRepository;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TopicExchange exchange;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TradeJpaRepository tradeRepository;

    @PostMapping("/send")
    public String sendTrade(@RequestBody SqlTrade trade) {
        try {
            String tradeJson = objectMapper.writeValueAsString(trade);
            rabbitTemplate.convertAndSend(exchange.getName(), routingKey, tradeJson);
            return "Trade sent successfully!";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error sending trade.";
        }
    }

    @GetMapping
    public List<SqlTrade> getAllTrades() {
        return tradeRepository.findAll();
    }
}
