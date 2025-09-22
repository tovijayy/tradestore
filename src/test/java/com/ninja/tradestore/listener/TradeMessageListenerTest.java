package com.ninja.tradestore.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninja.tradestore.entity.SqlTrade;

import com.ninja.tradestore.service.SQLTradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeMessageListenerTest {

    @Mock
    private SQLTradeService tradeService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TradeMessageListener tradeMessageListener;

    @Test
    void receiveMessage_shouldProcessTrade_whenMessageIsValid() throws Exception {
        String message = "{\"tradeId\":\"T1\",\"version\":1,\"maturityDate\":\"2023-10-10\"}";
        SqlTrade trade = new SqlTrade();
        trade.setTradeId("T1");
        trade.setVersion(1);

        when(objectMapper.readValue(message, SqlTrade.class)).thenReturn(trade);

        tradeMessageListener.receiveMessage(message);

        verify(tradeService).processTrade(trade);
    }

    @Test
    void receiveMessage_shouldLogError_whenMessageIsInvalid() throws Exception {
        String message = "invalid-message";

        doThrow(new RuntimeException("Invalid JSON")).when(objectMapper).readValue(message, SqlTrade.class);

        tradeMessageListener.receiveMessage(message);

        verify(tradeService, never()).processTrade(any());
    }
}