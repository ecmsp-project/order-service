package com.ecmsp.orderservice.order.adapter.publisher.outbox;

import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.ecmsp.orderservice.order.domain.OrderEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


@Slf4j
public class OutboxEventProcessor {

    private final OutboxRepository outboxRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final ObjectMapper objectMapper;

    OutboxEventProcessor(OutboxRepository outboxRepository, OrderEventPublisher orderEventPublisher, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 100)
    public void processOutboxEvents() {
        try {
            List<OutboxEvent> unprocessedEvents = outboxRepository.findUnprocessedEvents();

            if (unprocessedEvents.isEmpty()) {
                return;
            }

            log.debug("Processing {} unprocessed outbox events", unprocessedEvents.size());

            for (OutboxEvent event : unprocessedEvents) {
                processEvent(event);
            }

        } catch (Exception e) {
            log.error("Error processing outbox events", e);
        }
    }

    private void processEvent(OutboxEvent event) {
        try {
            String eventType = event.eventType().value();
            Class<?> eventClass = Class.forName(eventType);
            Object deserializedEvent = objectMapper.readValue(event.payload(), eventClass);

            switch (deserializedEvent){
                case OrderEvent orderEvent -> orderEventPublisher.publish(orderEvent);
                default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
            }
            outboxRepository.markAsProcessed(event.eventId());
            log.info("Successfully processed outbox event: id={}, eventType={}",
                    event.eventId(), eventType);
        } catch (Exception e) {
            log.error("Failed to process outbox event - will retry: id={}, eventType={}",
                    event.eventId(), event.eventType(), e);
        }
    }

}
