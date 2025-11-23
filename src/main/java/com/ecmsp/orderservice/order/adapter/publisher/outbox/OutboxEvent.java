package com.ecmsp.orderservice.order.adapter.publisher.outbox;

import java.time.LocalDateTime;

public record OutboxEvent(
    EventId eventId,
    EventType eventType,
    String payload,
    LocalDateTime createdAt,
    boolean processed,
    LocalDateTime processedAt
) {

}
