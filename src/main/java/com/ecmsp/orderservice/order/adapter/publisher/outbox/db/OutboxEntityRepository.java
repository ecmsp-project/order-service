package com.ecmsp.orderservice.order.adapter.publisher.outbox.db;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
interface OutboxEntityRepository extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findByProcessedFalseOrderByCreatedAtAsc();

    @Modifying
    @Query("DELETE FROM OutboxEntity o WHERE o.processed = true AND o.processedAt < :before")
    void deleteProcessedEventsBefore(@Param("before") LocalDateTime before);

    @Modifying
    @Query("UPDATE OutboxEntity o SET o.processed = true, o.processedAt = :processedAt WHERE o.eventId = :eventId")
    void markAsProcessed(@Param("eventId") UUID eventId, @Param("processedAt") LocalDateTime processedAt);


}
