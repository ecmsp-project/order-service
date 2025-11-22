package com.ecmsp.orderservice.outbox.domain;

import java.util.UUID;

public record EventId(UUID value) {

    @Override
    public String toString() {
        return value.toString();
    }

}