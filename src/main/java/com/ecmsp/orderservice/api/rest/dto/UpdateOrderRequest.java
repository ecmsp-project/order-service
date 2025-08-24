package com.ecmsp.orderservice.api.rest.dto;

import com.ecmsp.orderservice.order.domain.OrderStatus;

public record UpdateOrderRequest(
    OrderStatus orderStatus
) {
}
