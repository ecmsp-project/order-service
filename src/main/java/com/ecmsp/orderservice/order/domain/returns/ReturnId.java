package com.ecmsp.orderservice.order.domain.returns;

import java.util.UUID;

public record ReturnId(UUID id) {
    @Override
    public String toString() {
        return id.toString();
    }
}
