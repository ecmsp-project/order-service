package com.ecmsp.orderservice.order.domain;

import java.math.BigDecimal;

public record OrderItem(
    ItemId itemId,
    VariantId variantId,
    String name,
    int quantity,
    BigDecimal price,
    String imageUrl,
    String description,
    boolean isReturnable
) {
}
