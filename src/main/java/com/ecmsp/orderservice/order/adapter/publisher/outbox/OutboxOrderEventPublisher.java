package com.ecmsp.orderservice.order.adapter.publisher.outbox;

import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

public class OutboxOrderEventPublisher implements OrderEventPublisher {

    private final OutboxRepository outboxRepository;
    private final Supplier<UUID> eventIdSupplier;
    private final ObjectMapper objectMapper;

    public OutboxOrderEventPublisher(OutboxRepository outboxRepository, Supplier<UUID> eventIdSupplier, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.eventIdSupplier = eventIdSupplier;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderEvent event) {
        UUID eventId = eventIdSupplier.get();
        OutboxEvent outboxEvent = new OutboxEvent(
                new EventId(eventId),
                new EventType(event.getClass().getName()),
                serializeEvent(event),
                LocalDateTime.now(),
                false,
                null
        );

        outboxRepository.save(outboxEvent);
    }

    private String serializeEvent(OrderEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
