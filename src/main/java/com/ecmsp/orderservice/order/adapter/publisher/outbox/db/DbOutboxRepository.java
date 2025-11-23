package com.ecmsp.orderservice.order.adapter.publisher.outbox.db;

import com.ecmsp.orderservice.order.adapter.publisher.outbox.OutboxEvent;
import com.ecmsp.orderservice.order.adapter.publisher.outbox.EventId;
import com.ecmsp.orderservice.order.adapter.publisher.outbox.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
class DbOutboxRepository implements OutboxRepository {

    private final OutboxEntityRepository outboxEntityRepository;

    private final OutboxEntityMapper outboxEntityMapper = new OutboxEntityMapper();

    DbOutboxRepository(OutboxEntityRepository outboxRepository) {
        this.outboxEntityRepository = outboxRepository;
    }

    @Override
    @Transactional
    public void save(OutboxEvent event) {
        OutboxEntity entity = outboxEntityMapper.toOutboxEntity(event);
        outboxEntityRepository.save(entity);
        log.debug("Saved outbox event: id={}, type={}", event.eventId(), event.eventType());
    }


    @Transactional(readOnly = true)
    public List<OutboxEvent> findUnprocessedEvents() {

        List<OutboxEntity> unprocessdEventEntities = outboxEntityRepository.findByProcessedFalseOrderByCreatedAtAsc();

        return unprocessdEventEntities.stream()
            .map(outboxEntityMapper::toOutboxEvent)
            .toList();
    }

    @Transactional
    public void markAsProcessed(EventId eventId) {
        outboxEntityRepository.markAsProcessed(eventId.value(), LocalDateTime.now());
        log.debug("Marked outbox event as processed: id={}", eventId);
    }


    @Transactional
    public void deleteProcessedEventsBefore(LocalDateTime before) {
        outboxEntityRepository.deleteProcessedEventsBefore(before);
        log.info("Deleted processed outbox events older than {}", before);
    }
}
