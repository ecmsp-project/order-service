package com.ecmsp.orderservice.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Order(
    OrderId orderId,
    ClientId clientId,
    OrderStatus orderStatus,
    LocalDateTime date,
    List<OrderItem> items
) {
    public BigDecimal totalPrice() {
        return items.stream()
                .map(OrderItem::priceAtTimeOfOrder)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
