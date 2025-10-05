package com.ecmsp.orderservice.order.domain;

import java.math.BigDecimal;

public record OrderItem(
    ItemId itemId,
    //TODO: we need to add variantId and make a call to product service: getVariantDetails(variantId) to get: price, image_url, description to present it to user or just keep all these fields in db and don't make another call after order is finalized -> I think 2nd option is better for performance
    int quantity,
    BigDecimal priceAtTimeOfOrder,
    boolean isReturnable
) {
}
