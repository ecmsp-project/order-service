package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.cart.CartItem;
import com.ecmsp.orderservice.order.domain.ClientId;
import com.ecmsp.orderservice.order.domain.OrderItem;
import com.ecmsp.orderservice.order.domain.OrderToCreate;

import java.util.List;



public record CartCreatedEvent(
        ClientId clientId,
        List<CartItem> items
) {

    public static OrderToCreate toOrder(CartCreatedEvent cartEvent) {
        return new OrderToCreate(
                /* clientId */ cartEvent.clientId(),
                /* items */ cartEvent.items().stream()
                .map(cartItem -> new OrderItem(
                        cartItem.itemId(),
                        cartItem.quantity(),
                        cartItem.price()
                )).toList()
        );
    }


}
