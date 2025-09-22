package com.ecmsp.orderservice.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public boolean isReturnable() {
        return isWithinReturnPeriod() && hasReturnableItems();
    }

    public boolean isWithinReturnPeriod() {
        return ChronoUnit.DAYS.between(date, LocalDateTime.now()) <= 14;
    }

    public boolean hasReturnableItems() {
        return items.stream().anyMatch(OrderItem::isReturnable);
    }

    public List<OrderItem> getReturnableItems() {
        if (!isWithinReturnPeriod()) {
            return List.of();
        }
        return items.stream()
                .filter(OrderItem::isReturnable)
                .toList();
    }

}
