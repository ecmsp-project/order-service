package com.ecmsp.orderservice.order.domain;

import com.ecmsp.orderservice.order.domain.reservation.ReservationId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record Order(
    OrderId orderId,
    ReservationId reservationId,
    ClientId clientId,
    OrderStatus orderStatus,
    LocalDateTime date,
    List<OrderItem> items
) {

    private static final int RETURNABLE_PERIOD_DAYS = 14;


    public BigDecimal totalPrice() {
        return items.stream()
                .map(OrderItem::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isReturnable() {
        return isWithinReturnPeriod() && hasReturnableItems();
    }

    public boolean isWithinReturnPeriod() {
        return ChronoUnit.DAYS.between(date, LocalDateTime.now()) <= RETURNABLE_PERIOD_DAYS;
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
