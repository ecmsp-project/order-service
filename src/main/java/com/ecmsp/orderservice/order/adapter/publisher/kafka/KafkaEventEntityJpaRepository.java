package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
    name = "order.event-publisher.outbox-type",
    havingValue = "db"
)
public interface KafkaEventEntityJpaRepository extends JpaRepository<KafkaEventEntity, UUID>  {

    List<KafkaEventEntity> findByProcessedFalseOrderByCreatedAtAsc();

    @Transactional
    @Modifying
    @Query("DELETE FROM KafkaEventEntity o WHERE o.processed = true AND o.processedAt < :before")
    void deleteProcessedEventsBefore(@Param("before") LocalDateTime before);

    @Transactional
    @Modifying
    @Query("UPDATE KafkaEventEntity o SET o.processed = true, o.processedAt = :processedAt WHERE o.eventId = :eventId")
    void markAsProcessed(@Param("eventId") UUID eventId, @Param("processedAt") LocalDateTime processedAt);


}
