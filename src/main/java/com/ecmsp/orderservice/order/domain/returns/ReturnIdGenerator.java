package com.ecmsp.orderservice.order.domain.returns;
import com.ecmsp.orderservice.order.domain.CorrelationId;

public interface ReturnIdGenerator {
    ReturnId generate(CorrelationId correlationId);
}
