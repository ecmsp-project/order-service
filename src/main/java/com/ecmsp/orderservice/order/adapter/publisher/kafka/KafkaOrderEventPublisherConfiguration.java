package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
    prefix = "order.event-publisher",
    name = "type",
    havingValue = "kafka")
class KafkaOrderEventPublisherConfiguration {

    @Bean
    Outbox outbox(
        KafkaEventEntityRepository kafkaEventEntityRepository,
        @Value("${kafka.topic.order-status-updated}") String orderStatusUpdatedTopic,
        @Value("${kafka.topic.order-created}") String orderCreatedTopic,
        ObjectMapper objectMapper
    ) {
        return new Outbox(kafkaEventEntityRepository, objectMapper, orderStatusUpdatedTopic, orderCreatedTopic);
    }

    @Bean
    OutboxProcessor outboxProcessor(
        KafkaEventEntityRepository kafkaEventEntityRepository,
        KafkaTemplate<String, String> kafkaTemplate
    ) {
        return new OutboxProcessor(kafkaEventEntityRepository, kafkaTemplate);
    }

    @Bean
    OrderEventPublisher orderEventPublisher(Outbox outbox) {
        return new KafkaOrderEventPublisher(outbox);
    }

    @Bean
    @ConditionalOnProperty(
        name = "order.event-publisher.outbox-type",
        havingValue = "in-memory"
    )
    KafkaEventEntityRepository inMemoryKafkaEventEntityRepository() {
        return new InMemoryEventEntityRepository();
    }

}
