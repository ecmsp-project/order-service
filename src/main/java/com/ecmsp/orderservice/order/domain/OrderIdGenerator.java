package com.ecmsp.orderservice.order.domain;

public interface OrderIdGenerator {
    OrderId generate(CorrelationId correlationId);
}
