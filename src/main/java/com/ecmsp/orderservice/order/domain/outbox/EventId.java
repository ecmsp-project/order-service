package com.ecmsp.orderservice.order.domain.outbox;

import java.util.UUID;

public record EventId(UUID value) {

    @Override
    public String toString() {
        return value.toString();
    }

}