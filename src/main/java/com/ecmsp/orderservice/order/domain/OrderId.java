package com.ecmsp.orderservice.order.domain;

import java.util.UUID;

public record OrderId(UUID value) {

    @Override
    public String toString() {
        return value.toString();
    }

    public static OrderId from(String orderId) {
        return new OrderId(UUID.fromString(orderId));
    }

}
