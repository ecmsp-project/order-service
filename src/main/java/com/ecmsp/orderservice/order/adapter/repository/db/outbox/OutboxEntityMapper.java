package com.ecmsp.orderservice.order.adapter.repository.db.outbox;

import com.ecmsp.orderservice.order.domain.outbox.EventId;
import com.ecmsp.orderservice.order.domain.outbox.EventType;
import com.ecmsp.orderservice.order.domain.outbox.OutboxEvent;

import java.time.LocalDateTime;

class OutboxEntityMapper {


    public OutboxEvent toOutboxEvent(OutboxEntity entity) {
        return new OutboxEvent(
            new EventId(entity.getEventId()),
            new EventType(entity.getEventType()),
            entity.getPayload(),
            entity.getCreatedAt(),
            entity.isProcessed(),
            entity.getProcessedAt()
        );
    }

    public OutboxEntity toOutboxEntity(OutboxEvent event) {
        return OutboxEntity.builder()
            .eventId(event.eventId().value())
            .eventType(event.eventType().value())
            .payload(event.payload())
            .createdAt(event.createdAt())
            .processed(event.processed())
            .processedAt(event.processedAt())
            .build();
    }


}
