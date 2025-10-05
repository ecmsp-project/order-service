package com.ecmsp.orderservice.order.domain.returns;

import com.ecmsp.orderservice.order.domain.OrderId;

import java.util.List;

public record ReturnToCreate(
        OrderId orderId,
        List<ItemToReturnDetails> itemsToReturn
) {}
