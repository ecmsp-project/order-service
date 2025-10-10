package com.ecmsp.orderservice.api.kafka;

public record KafkaPaymentProcessedFailedEvent(
        String orderId,
        String paymentId,
        String processedAt
) {

}

