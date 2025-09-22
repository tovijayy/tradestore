package com.ninja.tradestore;

import com.ninja.tradestore.entity.SqlTrade;
import com.ninja.tradestore.model.TradeDocument;
import com.ninja.tradestore.repository.TradeHazelcastRepository;
import com.ninja.tradestore.repository.TradeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import org.awaitility.Awaitility;

@SpringBootTest
@Testcontainers
class TradeIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TradeJpaRepository tradeRepository; // H2 (SQL)

    @Autowired
    private TradeHazelcastRepository tradeHazelcastRepository; // Hazelcast (NoSQL)

    @BeforeEach
    void setUp() {
        SqlTrade trade = new SqlTrade();
        trade.setTradeId("T1");
        trade.setVersion(1);
        trade.setCounterPartyId("CP-1");
        trade.setBookId("BOOK001");
        trade.setMaturityDate(LocalDate.of(2025, Month.SEPTEMBER,25));
        trade.setExpired('N');
        tradeRepository.save(trade);
    }


    @Test
    void testTradeMessageConsumedAndPersisted() {
        // given
        TradeDocument trade = createTestTrade("T1", 1);

        // when: publish trade event
        rabbitTemplate.convertAndSend("trade.exchange", "trade.routingKey", trade);

        // then: wait until both storages reflect the trade
        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // H2 check
            Optional<SqlTrade> sqlTrade = tradeRepository.findTopByTradeIdOrderByVersionDesc("T1");
            assertThat(sqlTrade).isPresent();
            assertThat(sqlTrade.get().getBookId()).isEqualTo("BOOK001");

            // Hazelcast check
            Collection<TradeDocument> trades = tradeHazelcastRepository.findAll();
            assertThat(trades).extracting("tradeId").contains("T1");
        });
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
}
