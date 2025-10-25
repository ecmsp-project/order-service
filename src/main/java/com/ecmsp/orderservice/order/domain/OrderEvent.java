package com.ecmsp.orderservice.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public sealed interface OrderEvent {

     record OrderStatusUpdated(
            OrderId orderId,
            OrderStatus status
    ) implements OrderEvent {}


    record OrderCreated(
        OrderId orderId,
        ClientId clientId,
        BigDecimal orderTotal,
        LocalDateTime requestedAt
    ) implements OrderEvent {}



}
