package com.ecmsp.orderservice.api.kafka;

record KafkaPaymentProcessedSucceededEvent(
        String orderId,
        String paymentId,
        String processedAt
) {

}
