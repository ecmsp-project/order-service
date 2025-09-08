package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderIdGenerator;

import java.util.Optional;
import java.util.UUID;

class FixedOrderIdGenerator implements OrderIdGenerator {

    private final OrderIdMappingService mappingService;

    FixedOrderIdGenerator(OrderIdMappingService mappingService) {
        this.mappingService = mappingService;
    }

    @Override
    public OrderId generate(CorrelationId correlationId) {

        CorrelationId effectiveCorrelationId = Optional.ofNullable(correlationId)
                .orElse(new CorrelationId(UUID.randomUUID()));

       return mappingService.getOrderIdByCorrelation(effectiveCorrelationId)
               .orElseGet(() -> createNewOrderId(effectiveCorrelationId));
    }

    private OrderId createNewOrderId(CorrelationId correlationId) {
        OrderId newOrderId = new OrderId(correlationId.value());
        mappingService.mapCorrelationToOrder(correlationId, newOrderId);
        return newOrderId;
    }
}
