package com.ecmsp.orderservice.order.domain.reservation;

import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.VariantId;

import java.util.List;


public record ReservationToCreate(
        OrderId orderId,
        List<VariantToReserve> variantsToReserve

) {
    public record VariantToReserve(
            VariantId variantId,
            int quantity
    ){}
}


