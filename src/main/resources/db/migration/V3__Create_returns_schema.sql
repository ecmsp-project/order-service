-- V3__Create_returns_schema.sql
-- Flyway migration to create returns and return_item tables

CREATE TABLE returns
(
    return_id  UUID PRIMARY KEY,
    order_id   UUID        NOT NULL,
    status     VARCHAR(30) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    CONSTRAINT fk_returns_order
        FOREIGN KEY (order_id)
            REFERENCES orders (order_id)
            ON DELETE CASCADE
);

CREATE TABLE return_item
(
    return_item_id UUID PRIMARY KEY,
    item_id        UUID    NOT NULL,
    variant_id     UUID    NOT NULL,
    return_id      UUID    NOT NULL,
    quantity       INTEGER NOT NULL,
    reason         VARCHAR,
    CONSTRAINT fk_return_item_return
        FOREIGN KEY (return_id)
            REFERENCES returns (return_id)
            ON DELETE CASCADE
);

CREATE INDEX idx_returns_order_id ON returns (order_id);
CREATE INDEX idx_returns_status ON returns (status);
CREATE INDEX idx_returns_created_at ON returns (created_at);
CREATE INDEX idx_return_item_return_id ON return_item (return_id);
