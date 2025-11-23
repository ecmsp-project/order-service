package com.ecmsp.orderservice.order.adapter.publisher.outbox;

public record EventType(String value) {

    @Override
    public String toString() {
        return value;
    }

}
