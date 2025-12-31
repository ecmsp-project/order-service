package com.ecmsp.orderservice.order.domain.returns;

import com.ecmsp.orderservice.order.domain.OrderId;

import java.util.List;
import java.util.Optional;

public interface ReturnRepository {
    ReturnOrder save(ReturnOrder returnOrderDomain);
    Optional<ReturnOrder> findById(ReturnId returnId);
    List<ReturnOrder> findAll();
    List<ReturnOrder> findByOrderId(OrderId orderId);
}
