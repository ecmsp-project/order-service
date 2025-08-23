package com.ecmsp.orderservice.api.kafka;

public record PaymentProcessedKafkaEventSucceeded(
        String orderId,
        String paymentId,
        String processedAt
) {

}
