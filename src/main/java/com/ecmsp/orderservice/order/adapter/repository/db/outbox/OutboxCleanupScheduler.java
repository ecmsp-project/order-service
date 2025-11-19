package com.ecmsp.orderservice.order.adapter.repository.db.outbox;

import com.ecmsp.orderservice.order.domain.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

//TODO: move it somewhere else and remove annotations
@Service
@RequiredArgsConstructor
@Slf4j
class OutboxCleanupScheduler {

    private final OutboxRepository outboxRepository;

    @Value("${outbox.cleanup.retention-days:7}")
    private int retentionDays;

    @Scheduled(cron = "${outbox.cleanup.cron:0 0 2 * * *}")
    public void cleanupProcessedEvents() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);

            log.info("Starting cleanup of processed outbox events older than {} days (before {})",
                    retentionDays, cutoffDate);

            outboxRepository.deleteProcessedEventsBefore(cutoffDate);

            log.info("Completed cleanup of processed outbox events");

        } catch (Exception e) {
            log.error("Error during outbox cleanup", e);
        }
    }

}
