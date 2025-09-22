package com.ninja.tradestore.repository;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.ninja.tradestore.model.TradeDocument;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeHazelcastRepositoryTest {

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IMap<String, TradeDocument> tradeMap;

    private TradeHazelcastRepository repository;

    private static final String MAP_NAME = "test-trades";
    private static final String TRADE_ID = "TRADE001";
    private static final Integer VERSION = 1;
    private static final String COMPOSITE_KEY = "TRADE001_1";

    @BeforeEach
    void setUp() {
        lenient().when(hazelcastInstance.<String, TradeDocument>getMap(MAP_NAME)).thenReturn(tradeMap);
        repository = new TradeHazelcastRepository(hazelcastInstance, MAP_NAME);
    }

    private TradeDocument createTestTrade(String tradeId, Integer version) {
        return TradeDocument.builder()
                .tradeId(tradeId)
                .version(version)
                .counterPartyId("CP001")
                .bookId("BOOK001")
                .maturityDate(LocalDate.now().plusYears(1))
                .createdDate(LocalDate.now())
                .expired('N')
                .build();
    }

    @Test
    void shouldSaveTradeDocument() {
        TradeDocument trade = createTestTrade(TRADE_ID, VERSION);
        //lenient().when(trade.getCompositeKey()).thenReturn(COMPOSITE_KEY);

        TradeDocument result = repository.save(trade);

        verify(tradeMap).put(COMPOSITE_KEY, trade);
        assertEquals(trade, result);
    }

    @Test
    void shouldThrowExceptionWhenSavingNullTradeDocument() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(null)
        );
        assertEquals("Trade document cannot be null", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenTradeExistsByIdAndVersion() {
        // Given
        when(tradeMap.containsKey(COMPOSITE_KEY)).thenReturn(true);

        // When
        boolean exists = repository.existsByTradeIdAndVersion(TRADE_ID, VERSION);

        // Then
        assertTrue(exists);
        verify(tradeMap).containsKey(COMPOSITE_KEY);
    }

    @Test
    void shouldFindTradeByCompositeKey() {
        TradeDocument trade = createTestTrade(TRADE_ID, VERSION);
        when(tradeMap.get(COMPOSITE_KEY)).thenReturn(trade);

        Optional<TradeDocument> result = repository.findByCompositeKey(COMPOSITE_KEY);

        assertTrue(result.isPresent());
        assertEquals(trade, result.get());
        verify(tradeMap).get(COMPOSITE_KEY);
    }

}
