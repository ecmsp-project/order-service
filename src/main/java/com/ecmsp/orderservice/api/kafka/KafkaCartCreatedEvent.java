package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public record KafkaCartCreatedEvent(
        String clientId,
        List<CartItem> items
) {

    public record CartItem(
        String itemId,
        String variantId,
        String name,
        BigDecimal price,
        int quantity,
        String imageUrl,
        String description,
        boolean isReturnable
    ) {
    }

    public static OrderToCreate toOrder(KafkaCartCreatedEvent cartEvent) {
        return new OrderToCreate(
                /* reservationId */ null,
                /* clientId */ new ClientId(UUID.fromString(cartEvent.clientId())),
                /* items */ cartEvent.items().stream()
                .map(cartItem -> new OrderItem(
                        new ItemId(UUID.fromString(cartItem.itemId())),
                        new VariantId(UUID.fromString(cartItem.variantId())),
                        cartItem.quantity(),
                        cartItem.price(),
                        cartItem.imageUrl(),
                        cartItem.description(),
                        cartItem.isReturnable()
                )).toList()
        );
    }


}
