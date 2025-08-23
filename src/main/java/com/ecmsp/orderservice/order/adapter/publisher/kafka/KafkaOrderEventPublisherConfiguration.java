package com.ecmsp.orderservice.order.adapter.publisher.kafka;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
        prefix = "order.event-publisher",
        name = "type",
        havingValue = "kafka")
public class KafkaOrderEventPublisherConfiguration {

    @Bean
    public KafkaOrderEventPublisher kafkaOrderEventPublisher(KafkaTemplate<String, KafkaOrderStatusUpdatedEvent> kafkaTemplate) {
        return new KafkaOrderEventPublisher(kafkaTemplate);
    }

}
