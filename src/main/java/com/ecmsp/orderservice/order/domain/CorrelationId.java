package com.ecmsp.orderservice.order.domain;

import java.util.UUID;

public record CorrelationId(UUID value) {

    @Override
    public String toString() {
        return value.toString();
    }




}