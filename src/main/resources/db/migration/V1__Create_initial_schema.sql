
CREATE TABLE orders
(
    order_id       UUID PRIMARY KEY,
    reservation_id UUID,
    order_status   VARCHAR(30) NOT NULL,
    date           TIMESTAMP   NOT NULL,
    client_id      UUID        NOT NULL
);


CREATE TABLE order_item
(
    order_item_id UUID PRIMARY KEY,
    item_id       UUID    NOT NULL,
    variant_id    UUID,
    order_id      UUID    NOT NULL,
    item_name     VARCHAR NOT NULL,
    quantity      INTEGER NOT NULL,
    price         DECIMAL NOT NULL,
    image_url     VARCHAR,
    description   VARCHAR,
    is_returnable BOOLEAN NOT NULL,
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id)
            REFERENCES orders (order_id)
            ON DELETE CASCADE
);


CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_orders_client_id ON orders (client_id);
CREATE INDEX idx_orders_status ON orders (order_status);
CREATE INDEX idx_orders_date ON orders (date);