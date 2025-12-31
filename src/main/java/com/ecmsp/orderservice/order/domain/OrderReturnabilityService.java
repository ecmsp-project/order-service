package com.ecmsp.orderservice.order.domain;

import java.util.List;

public class OrderReturnabilityService {

    private final OrderRepository orderRepository;

    public OrderReturnabilityService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean canOrderBeReturned(OrderId orderId) {
        return orderRepository.findById(orderId)
                .map(Order::isReturnable)
                .orElse(false);
    }

    public List<OrderItem> getReturnableItems(OrderId orderId) {
        return orderRepository.findById(orderId)
                .map(Order::getReturnableItems)
                .orElse(List.of());
    }
}