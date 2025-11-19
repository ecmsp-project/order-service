package com.ecmsp.orderservice.order.domain.outbox;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository {

    void save(OutboxEvent event);

    List<OutboxEvent> findUnprocessedEvents();

    void markAsProcessed(EventId eventId);

    void deleteProcessedEventsBefore(LocalDateTime before);

}
