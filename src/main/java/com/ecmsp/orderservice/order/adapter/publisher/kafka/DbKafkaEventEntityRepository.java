package com.ecmsp.orderservice.order.adapter.publisher.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
    name = "order.event-publisher.outbox-type",
    havingValue = "db"
)
interface DbKafkaEventEntityRepository extends JpaRepository<KafkaEventEntity, UUID>, KafkaEventEntityRepository  {

    List<KafkaEventEntity> findByProcessedFalseOrderByCreatedAtAsc();

    @Modifying
    @Query("DELETE FROM KafkaEventEntity o WHERE o.processed = true AND o.processedAt < :before")
    void deleteProcessedEventsBefore(@Param("before") LocalDateTime before);

    @Modifying
    @Query("UPDATE KafkaEventEntity o SET o.processed = true, o.processedAt = :processedAt WHERE o.eventId = :eventId")
    void markAsProcessed(@Param("eventId") UUID eventId, @Param("processedAt") LocalDateTime processedAt);


}
