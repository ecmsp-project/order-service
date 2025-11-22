package com.ecmsp.orderservice.outbox.adapter.repository;

import com.ecmsp.orderservice.outbox.domain.EventId;
import com.ecmsp.orderservice.outbox.domain.EventType;
import com.ecmsp.orderservice.outbox.domain.OutboxEvent;

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
