package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnIdGenerator;

import java.util.Optional;
import java.util.UUID;

class FixedReturnIdGenerator implements ReturnIdGenerator {

    private final ReturnIdMappingService mappingService;

    FixedReturnIdGenerator(ReturnIdMappingService mappingService) {
        this.mappingService = mappingService;
    }

    @Override
    public ReturnId generate(CorrelationId correlationId) {
        CorrelationId effectiveCorrelationId = Optional.ofNullable(correlationId)
                .orElse(new CorrelationId(UUID.randomUUID()));

        return mappingService.getReturnIdByCorrelation(effectiveCorrelationId)
                .orElseGet(() -> createNewReturnId(effectiveCorrelationId));
    }

    private ReturnId createNewReturnId(CorrelationId correlationId) {
        ReturnId newReturnId = new ReturnId(correlationId.value());
        mappingService.mapCorrelationToReturn(correlationId, newReturnId);
        return newReturnId;
    }
}
