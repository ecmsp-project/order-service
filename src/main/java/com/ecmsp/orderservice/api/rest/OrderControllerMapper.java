package com.ecmsp.orderservice.api.rest;

import com.ecmsp.orderservice.api.dto.OrderDetailsResponse;
import com.ecmsp.orderservice.order.domain.Order;

public class OrderControllerMapper {

    OrderDetailsResponse toOrderDetailsResponse(Order order) {
        return new OrderDetailsResponse(
            /* orderId = */ order.orderId().value(),
            /* clientId = */ order.clientId().value(),
            /* orderStatus = */ order.orderStatus().name(),
            /* date = */ order.date(),
            /* items = */ order.items().stream()
                .map(item ->
                    new OrderDetailsResponse.OrderItemDetails(
                        /* itemId = */ item.itemId().value(),
                        /* quantity = */ item.quantity()
                    )
                )
                .toList()
        );
    }

}
