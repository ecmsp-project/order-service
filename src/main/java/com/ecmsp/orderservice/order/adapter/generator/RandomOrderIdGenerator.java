package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.*;

import java.util.UUID;

class RandomOrderIdGenerator implements OrderIdGenerator {

    @Override
    public OrderId generate(CorrelationId correlationId) {
        return new OrderId(UUID.randomUUID());

    }
}
