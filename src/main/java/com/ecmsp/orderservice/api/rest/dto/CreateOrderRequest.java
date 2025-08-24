package com.ecmsp.orderservice.api.rest.dto;

import com.ecmsp.orderservice.order.domain.ItemId;
import com.ecmsp.orderservice.order.domain.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        UUID clientId,
        List<Item> items
) {


    public record Item(
            UUID itemId,
            int quantity,
            double price
    ) {
        public OrderItem toOrderItem() {
            return new OrderItem(new ItemId(itemId), quantity, BigDecimal.valueOf(price));
        }
    }

}
