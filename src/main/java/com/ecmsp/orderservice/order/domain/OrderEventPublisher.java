package com.ecmsp.orderservice.order.domain;

public interface OrderEventPublisher {
    void publish(OrderEvent event);
}
