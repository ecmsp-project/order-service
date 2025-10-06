package com.ecmsp.orderservice.order.domain.returns;

import com.ecmsp.orderservice.order.domain.OrderId;

import java.time.LocalDateTime;
import java.util.List;

public record ReturnOrder(
        ReturnId returnId,
        OrderId orderId,
        List<ItemToReturnDetails> itemsToReturn,
        ReturnStatus status,
        LocalDateTime createdAt
) {
}
