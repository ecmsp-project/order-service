package com.ecmsp.orderservice.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentToCreate(
        OrderId orderId,
        ClientId clientId,
        BigDecimal amount,
        LocalDateTime requestedAt
) {
}
