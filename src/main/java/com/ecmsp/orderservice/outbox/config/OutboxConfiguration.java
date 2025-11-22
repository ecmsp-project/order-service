package com.ecmsp.orderservice.outbox.config;

import com.ecmsp.orderservice.order.adapter.publisher.kafka.KafkaOrderEventPublisher;
import com.ecmsp.orderservice.outbox.domain.OutboxEventProcessor;
import com.ecmsp.orderservice.outbox.domain.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutboxConfiguration {

    @Bean
    OutboxEventProcessor outboxEventProcessor(
            OutboxRepository outboxRepository,
            //todo should I put here kafkaOrderEventPublisher or OutboxOrderEventPublisher or interface OrderEventPublisher
            // do I break hexagon by putting kafkaOrderEventPublisher here?Ś›
            KafkaOrderEventPublisher kafkaOrderEventPublisher,
            ObjectMapper objectMapper
    ){
        return new OutboxEventProcessor(
                /* outboxRepository = */ outboxRepository,
                /* orderEventPublisher = */ kafkaOrderEventPublisher,
                /* objectMapper = */ objectMapper
        );
    }
}
