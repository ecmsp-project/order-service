package com.ecmsp.orderservice.api.rest.order.dto;

import com.ecmsp.orderservice.order.domain.OrderStatus;

public record UpdateOrderRequest(
    OrderStatus orderStatus
) {
}
