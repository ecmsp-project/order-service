package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.ClientId;
import com.ecmsp.orderservice.order.domain.ItemId;
import com.ecmsp.orderservice.order.domain.OrderItem;
import com.ecmsp.orderservice.order.domain.OrderToCreate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public record CartCreatedEvent(
        String clientId,
        List<CartItem> items
) {

    public record CartItem(
        String itemId,
        String name,
        BigDecimal price,
        int quantity,
        String description,
        boolean isReturnable
    ) {
    }

    public static OrderToCreate toOrder(CartCreatedEvent cartEvent) {
        return new OrderToCreate(
                /* clientId */ new ClientId(UUID.fromString(cartEvent.clientId())),
                /* items */ cartEvent.items().stream()
                .map(cartItem -> new OrderItem(
                        new ItemId(UUID.fromString(cartItem.itemId())),
                        cartItem.quantity(),
                        cartItem.price(),
                        cartItem.isReturnable()
                )).toList()
        );
    }


}
