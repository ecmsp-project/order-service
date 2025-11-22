package com.ecmsp.orderservice.outbox.domain;

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
