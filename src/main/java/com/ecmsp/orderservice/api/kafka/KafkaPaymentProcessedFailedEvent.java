package com.ecmsp.orderservice.api.kafka;

record KafkaPaymentProcessedFailedEvent(
        String orderId,
        String paymentId,
        String processedAt
) {

}

