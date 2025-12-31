package com.ecmsp.orderservice.api.rest.order.dto;

import com.ecmsp.orderservice.order.domain.ItemId;
import com.ecmsp.orderservice.order.domain.OrderItem;
import com.ecmsp.orderservice.order.domain.VariantId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        UUID variantId,
        UUID clientId,
        List<Item> items
) {


    public record Item(
            UUID itemId,
            UUID variantId,
            String name,
            int quantity,
            double price,
            String imageUrl,
            String description,
            boolean returnable
    ) {
        public OrderItem toOrderItem() {
            return new OrderItem(
                new ItemId(itemId),
                new VariantId(variantId),
                name,
                quantity,
                BigDecimal.valueOf(price),
                imageUrl,
                description,
                returnable
            );
        }
    }

}
