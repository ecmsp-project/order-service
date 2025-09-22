package com.ecmsp.orderservice.api.rest.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderReturnabilityResponse(
    UUID orderId,
    boolean isReturnable,
    boolean isWithinReturnPeriod,
    List<ReturnableItemDto> returnableItems
) {
    public record ReturnableItemDto(
        UUID itemId,
        int quantity,
        BigDecimal priceAtTimeOfOrder
    ) {}
}