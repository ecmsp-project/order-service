package com.ecmsp.orderservice.order.domain;

public record OrderToUpdate(
    OrderId orderId,
    OrderStatus newStatus
) {
}
