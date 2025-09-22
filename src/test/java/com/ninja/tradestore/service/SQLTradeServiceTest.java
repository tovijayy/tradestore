package com.ninja.tradestore.service;

import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.repository.TradeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQLTradeServiceTest {

    @Mock
    private TradeJpaRepository tradeRepository;

    @Mock
    private TradeHazelcastService tradeHazelcastService;

    @InjectMocks
    private SQLTradeService sqlTradeService;

    @Test
    void processTrade_shouldSaveTrade_whenNoExistingTradeAndMaturityDateIsValid() {
        SqlTrade trade = new SqlTrade();
        trade.setTradeId("T1");
        trade.setVersion(1);
        trade.setMaturityDate(LocalDate.now().plusDays(1));

        when(tradeRepository.findTopByTradeIdOrderByVersionDesc("T1")).thenReturn(Optional.empty());

        sqlTradeService.processTrade(trade);

        verify(tradeRepository).save(trade);
        verify(tradeHazelcastService).saveOrUpdateTrade(trade);
    }

    @Test
    void expireTrades_shouldNotUpdateExpiredFlag_whenTradeIsAlreadyExpired() {
        SqlTrade trade = new SqlTrade();
        trade.setTradeId("T1");
        trade.setVersion(1);
        trade.setMaturityDate(LocalDate.now().minusDays(1));
        trade.setExpired('Y');

        when(tradeRepository.findByMaturityDateBefore(LocalDate.now())).thenReturn(List.of(trade));

        sqlTradeService.expireTrades();

        verify(tradeRepository, never()).save(trade);
        verify(tradeHazelcastService, never()).saveOrUpdateTrade(trade);
    }

    @Test
    void expireTrades_shouldNotUpdateHazelcast_whenNoTradeInHazelcast() {
        SqlTrade trade = new SqlTrade();
        trade.setTradeId("T1");
        trade.setVersion(1);
        trade.setMaturityDate(LocalDate.now().minusDays(1));
        trade.setExpired('N');

        when(tradeRepository.findByMaturityDateBefore(LocalDate.now())).thenReturn(List.of(trade));
        when(tradeHazelcastService.findByCompositeKey("T1_1")).thenReturn(Optional.empty());

        sqlTradeService.expireTrades();

        assertEquals('Y', trade.getExpired());
        verify(tradeRepository).save(trade);
        verify(tradeHazelcastService, never()).saveOrUpdateTrade(trade);
    }
}