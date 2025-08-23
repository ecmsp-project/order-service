package com.ecmsp.orderservice.order.domain;

import java.math.BigDecimal;

public record OrderItem(
    ItemId itemId,
    int quantity,
    BigDecimal priceAtTimeOfOrder
) {
}
