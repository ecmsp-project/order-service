package com.ecmsp.orderservice.order.adapter.payment.kafka;

import java.math.BigDecimal;

public record PaymentRequestedKafkaEvent(
        String orderId,
        String clientId,
        BigDecimal amount,
        String requestedAt
) {
}
