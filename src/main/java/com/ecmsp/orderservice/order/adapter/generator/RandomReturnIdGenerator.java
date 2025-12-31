package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnIdGenerator;

import java.util.UUID;

class RandomReturnIdGenerator implements ReturnIdGenerator {

    @Override
    public ReturnId generate(CorrelationId correlationId) {
        return new ReturnId(UUID.randomUUID());
    }
}
