package com.ecmsp.orderservice.cart;

import com.ecmsp.orderservice.order.domain.ItemId;

import java.math.BigDecimal;


public record CartItem(
    ItemId itemId,
    String name,
    BigDecimal price,
    int quantity,
    String description
) {
}
