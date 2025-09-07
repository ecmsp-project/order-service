package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderIdGenerator;

import java.util.UUID;

class FixedOrderIdGenerator implements OrderIdGenerator {


    @Override
    public OrderId generate(CorrelationId correlationId) {
        if (correlationId == null || correlationId.value() == null) {
            throw new IllegalArgumentException("CorrelationId is required for fixed order ID generation");
        }
        // if OrderId with this value already exists it should return existing one

        return new OrderId(correlationId.value());
    }
}
