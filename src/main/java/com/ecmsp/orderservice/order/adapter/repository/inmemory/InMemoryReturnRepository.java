package com.ecmsp.orderservice.order.adapter.repository.inmemory;

import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnOrder;
import com.ecmsp.orderservice.order.domain.returns.ReturnRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class InMemoryReturnRepository implements ReturnRepository {

    private final Map<ReturnId, ReturnOrder> returns = new HashMap<>();

    @Override
    public ReturnOrder save(ReturnOrder returnOrderDomain) {
        returns.put(returnOrderDomain.returnId(), returnOrderDomain);
        return returnOrderDomain;
    }

    @Override
    public Optional<ReturnOrder> findById(ReturnId returnId) {
        return Optional.ofNullable(returns.get(returnId));
    }

    @Override
    public List<ReturnOrder> findAll() {
        return returns.values().stream().toList();
    }

    @Override
    public List<ReturnOrder> findByOrderId(OrderId orderId) {
        return returns.values().stream()
            .filter(returnOrder -> returnOrder.orderId().equals(orderId))
            .toList();
    }
}
