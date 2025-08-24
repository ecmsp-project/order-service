package com.ecmsp.orderservice.order.adapter.publisher.inmemory;

import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "order.event-publisher",
    name = "type",
    havingValue = "in-memory")
class InMemoryEventPublisherConfiguration {

    @Bean
    OrderEventPublisher inMemoryEventPublisher() {
        return new InMemoryEventPublisher();
    }
}
