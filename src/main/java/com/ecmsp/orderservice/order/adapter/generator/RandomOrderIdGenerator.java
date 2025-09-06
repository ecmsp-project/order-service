package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderIdGenerator;

import java.util.UUID;

public class RandomOrderIdGenerator implements OrderIdGenerator {

    @Override
    public OrderId generate(CorrelationId correlationId) {
        return correlationId.value() != null ? new OrderId(correlationId.value()) : new OrderId(UUID.randomUUID());
    }
}
