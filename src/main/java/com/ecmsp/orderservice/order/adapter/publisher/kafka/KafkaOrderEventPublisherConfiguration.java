package com.ecmsp.orderservice.order.adapter.publisher.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
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
    public KafkaOrderEventPublisher kafkaOrderEventPublisher(
            KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> orderStatusUpdatedKafkaTemplate,
            KafkaTemplate<String, KafkaOrderCreatedEvent> orderCreatedKafkaTemplate,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        return new KafkaOrderEventPublisher(orderStatusUpdatedKafkaTemplate, orderCreatedKafkaTemplate, kafkaTemplate, objectMapper);
    }

}
