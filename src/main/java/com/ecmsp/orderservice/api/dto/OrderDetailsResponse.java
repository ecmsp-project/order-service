package com.ecmsp.orderservice.api.dto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailsResponse(
    UUID orderId,
    UUID clientId,
    String orderStatus,
    LocalDateTime date,
    List<OrderItemDetails> items
) {

    public record OrderItemDetails(
        UUID itemId,
        int quantity
    ) {
    }

}
