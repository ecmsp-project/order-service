package com.ecmsp.orderservice.api.kafka;

public record KafkaPaymentProcessedSucceededEvent(
        String orderId,
        String paymentId,
        String processedAt
) {

}
