package com.ecmsp.orderservice.order.domain;

import java.util.List;

public record OrderToCreate(
    ClientId clientId,
    List<OrderItem> items
) {
}
