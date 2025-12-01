package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DbKafkaEventEntityRepository implements KafkaEventEntityRepository{

    private final KafkaEventEntityJpaRepository jpaRepository;

    public DbKafkaEventEntityRepository(KafkaEventEntityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public List<KafkaEventEntity> findByProcessedFalseOrderByCreatedAtAsc() {
        return jpaRepository.findByProcessedFalseOrderByCreatedAtAsc();
    }

    @Override
    public void deleteProcessedEventsBefore(LocalDateTime before) {
        jpaRepository.deleteProcessedEventsBefore(before);
    }

    @Override
    public void markAsProcessed(UUID eventId, LocalDateTime processedAt) {
        jpaRepository.markAsProcessed(eventId, processedAt);
    }

    @Override
    public KafkaEventEntity save(KafkaEventEntity entity) {
        return jpaRepository.save(entity);
    }
}
