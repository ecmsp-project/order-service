-- V5__Create_kafka_outbox_table.sql
-- Flyway migration to create kafka_outbox table for transactional outbox pattern

CREATE TABLE kafka_outbox
(
    event_id     UUID PRIMARY KEY,
    event_type   VARCHAR(255) NOT NULL,
    payload      TEXT         NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    processed    BOOLEAN      NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP
);

CREATE INDEX idx_kafka_outbox_processed ON kafka_outbox (processed);
CREATE INDEX idx_kafka_outbox_created_at ON kafka_outbox (created_at);
CREATE INDEX idx_kafka_outbox_processed_at ON kafka_outbox (processed_at) WHERE processed = TRUE;
