package com.ecmsp.orderservice.api.kafka;

public record PaymentProcessedKafkaEventFailed(
        String orderId,
        String paymentId,
        String processedAt
) {

}

