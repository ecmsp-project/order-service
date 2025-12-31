package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderIdMappingService {
    private final Map<CorrelationId, OrderId> correlationToOrderIdMap = new ConcurrentHashMap<>();

    public Optional<OrderId> getOrderIdByCorrelation(CorrelationId correlationId) {
        return Optional.ofNullable(correlationToOrderIdMap.get(correlationId));
    }

     public void mapCorrelationToOrder(CorrelationId correlationId, OrderId orderId) {
        correlationToOrderIdMap.put(correlationId, orderId);
    }

    public void clear() {
        correlationToOrderIdMap.clear();
    }

}
