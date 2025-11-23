package com.ecmsp.orderservice.order.adapter.publisher.outbox.db;

import com.ecmsp.orderservice.order.adapter.publisher.outbox.OutboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "order.repository",
    name = "type",
    havingValue = "db")
class DbOutboxRepositoryConfiguration {

    @Bean
    OutboxRepository dbOutboxRepository(OutboxEntityRepository outboxEntityRepository) {
        return new DbOutboxRepository(outboxEntityRepository);
    }

}
