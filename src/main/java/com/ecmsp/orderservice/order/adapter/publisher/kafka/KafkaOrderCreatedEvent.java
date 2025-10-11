package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import java.math.BigDecimal;

record KafkaOrderCreatedEvent(
        String orderId,
        String clientId,
        BigDecimal orderTotal,
        String requestedAt
) {
}
