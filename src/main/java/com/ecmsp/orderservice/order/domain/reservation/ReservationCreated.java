package com.ecmsp.orderservice.order.domain.reservation;

import com.ecmsp.orderservice.order.domain.VariantId;

import java.util.List;

public record ReservationCreated(
        List<VariantId> reservedVariantIds,
        List<VariantOutOfStock> variantsOutOfStock
) {
    public record VariantOutOfStock(
            VariantId variantId,
            int requestedQuantity,
            int availableQuantity
    ){}
}
