package com.ecmsp.orderservice.order.adapter.publisher.outbox;

import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConditionalOnProperty(
        prefix = "order.event-publisher",
        name = "type",
        havingValue = "outbox")
public class OutboxConfiguration {


    @Bean
    public OutboxOrderEventPublisher outboxOrderEventPublisher(
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        return new OutboxOrderEventPublisher(
                /* outboxRepository = */ outboxRepository,
                /* objectMapper = */ objectMapper
        );
    }

    @Bean
    OutboxEventProcessor outboxEventProcessor(
            OutboxRepository outboxRepository,
            OrderEventPublisher orderEventPublisher,
            ObjectMapper objectMapper
    ){
        return new OutboxEventProcessor(
                /* outboxRepository = */ outboxRepository,
                /* orderEventPublisher = */  orderEventPublisher,
                /* objectMapper = */ objectMapper
        );
    }
}
