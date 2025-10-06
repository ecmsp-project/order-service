package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReturnIdMappingService {
    private final Map<CorrelationId, ReturnId> correlationToReturnIdMap = new ConcurrentHashMap<>();

    public Optional<ReturnId> getReturnIdByCorrelation(CorrelationId correlationId) {
        return Optional.ofNullable(correlationToReturnIdMap.get(correlationId));
    }

    public void mapCorrelationToReturn(CorrelationId correlationId, ReturnId returnId) {
        correlationToReturnIdMap.put(correlationId, returnId);
    }

    public void clear() {
        correlationToReturnIdMap.clear();
    }
}
