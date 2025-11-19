package com.ecmsp.orderservice.order.domain.outbox;

public record EventType(String value) {

    @Override
    public String toString() {
        return value;
    }

}
