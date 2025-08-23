package com.ecmsp.orderservice.order.adapter.payment.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
        prefix = "payment.client",
        name = "type",
        havingValue = "kafka")
public class KafkaPaymentClientConfiguration {

    @Bean
    public KafkaPaymentClient kafkaPaymentClient(KafkaTemplate<String, PaymentRequestedKafkaEvent> kafkaTemplate) {
        return new KafkaPaymentClient(kafkaTemplate);
    }
}
