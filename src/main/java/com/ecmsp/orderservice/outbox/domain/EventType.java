package com.ecmsp.orderservice.outbox.domain;

public record EventType(String value) {

    @Override
    public String toString() {
        return value;
    }

}
