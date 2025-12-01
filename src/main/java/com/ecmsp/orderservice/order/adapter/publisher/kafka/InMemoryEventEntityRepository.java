package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryEventEntityRepository implements KafkaEventEntityRepository {

    private final Map<UUID, KafkaEventEntity> events = new ConcurrentHashMap<>();

    @Override
    public List<KafkaEventEntity> findByProcessedFalseOrderByCreatedAtAsc() {
        return events.values().stream()
            .filter(event -> !event.isProcessed())
            .sorted(Comparator.comparing(KafkaEventEntity::getCreatedAt))
            .toList();
    }

    @Override
    public void deleteProcessedEventsBefore(LocalDateTime before) {
        var eventIdsToRemove = events.values().stream()
            .filter(KafkaEventEntity::isProcessed)
            .filter(event -> event.getProcessedAt().isBefore(before))
            .toList();

        eventIdsToRemove.forEach(events::remove);
    }

    @Override
    public void markAsProcessed(UUID eventId, LocalDateTime processedAt) {
        events.computeIfPresent(
            eventId,
            (ignored, event) ->
                event.toBuilder()
                    .processed(true)
                    .processedAt(processedAt)
                    .build()
        );
    }

    @Override
    public KafkaEventEntity save(KafkaEventEntity entity) {
        if(entity.getEventId() == null) {
            entity.setEventId(UUID.randomUUID());
        }

        if(entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }

        if(entity.getProcessedAt() == null) {
            entity.setProcessedAt(LocalDateTime.now());
        }

        events.put(entity.getEventId(), entity);

        return entity;
    }
}
