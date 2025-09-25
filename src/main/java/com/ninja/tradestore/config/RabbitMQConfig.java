package com.ninja.tradestore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    @Value("${app.rabbitmq.dlq.queue}")
    private String tradeDLQ;

    @Value("${app.rabbitmq.dlq.exchange}")
    private String tradeDLQExchange;

    @Value("${app.rabbitmq.dlq.routingkey}")
    private String tradeDLQroutingkey;


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // Retry logic
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3) // retry 3 times
                .backOffOptions(1000, 2.0, 10000) // initial 1s, double each time, max 10s
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, tradeDLQExchange, tradeDLQroutingkey)) // send to DLQ after retries
                .build());

        return factory;
    }


    @Bean
    Queue queue() {
        //return new Queue(queueName, false);
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", tradeDLQExchange)
                .withArgument("x-dead-letter-routing-key", tradeDLQroutingkey)
                .build();
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    TopicExchange exchangeDlq() {
        return new TopicExchange(tradeDLQExchange);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    Queue tradeDLQ() {
        return QueueBuilder.durable(tradeDLQ).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(tradeDLQ())
                .to(exchangeDlq())
                .with(tradeDLQroutingkey);
    }
}
