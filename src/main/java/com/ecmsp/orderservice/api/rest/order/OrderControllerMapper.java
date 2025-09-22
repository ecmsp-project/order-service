package com.ecmsp.orderservice.api.rest.order;

import com.ecmsp.orderservice.api.rest.order.dto.OrderDetailsResponse;
import com.ecmsp.orderservice.api.rest.order.dto.OrderReturnabilityResponse;
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

    OrderReturnabilityResponse toOrderReturnabilityResponse(Order order) {
        return new OrderReturnabilityResponse(
            /* orderId = */ order.orderId().value(),
            /* isReturnable = */ order.isReturnable(),
            /* isWithinReturnPeriod = */ order.isWithinReturnPeriod(),
            /* returnableItems = */ order.getReturnableItems().stream()
                .map(item -> new OrderReturnabilityResponse.ReturnableItemDto(
                    /* itemId = */ item.itemId().value(),
                    /* quantity = */ item.quantity(),
                    /* priceAtTimeOfOrder = */ item.priceAtTimeOfOrder()
                ))
                .toList()
        );
    }

}
