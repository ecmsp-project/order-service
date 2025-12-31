package com.ecmsp.orderservice.order.domain.returns;

import com.ecmsp.orderservice.order.domain.ItemId;
import com.ecmsp.orderservice.order.domain.VariantId;

public record ItemToReturnDetails(ItemId itemId, VariantId variantId, Integer quantity, String reason) {
}

