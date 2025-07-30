package com.ecmsp.orderservice.order.domain;

public sealed interface OrderEvent {

    public record OrderStatusUpdated(
            OrderId orderId,
            OrderStatus status
    ) implements OrderEvent {}



}
