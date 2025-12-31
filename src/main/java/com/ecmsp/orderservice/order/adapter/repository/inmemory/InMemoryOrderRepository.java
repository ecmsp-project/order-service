package com.ecmsp.orderservice.order.adapter.repository.inmemory;

import com.ecmsp.orderservice.order.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class InMemoryOrderRepository implements OrderRepository {

    // In-memory storage for orders
    private final Map<OrderId, Order> orders = new HashMap<>();

    @Override
    public void create(Order order) {
        if(orders.containsKey(order.orderId())) {
            throw new OrderException.AlreadyExists(order.orderId());
        }
        orders.put(order.orderId(), order);
    }

    @Override
    public void update(Order order) {
        if (!orders.containsKey(order.orderId())) {
            throw new OrderException.NotFound(order.orderId());
        }
        orders.put(order.orderId(), order);
    }

    @Override
    public void deleteById(OrderId orderId) {
        orders.remove(orderId);
    }

    @Override
    public List<Order> findByClientId(ClientId clientId) {
        return List.of();
    }

    @Override
    public List<Order> findAll() {
        return orders.values().stream().toList();
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

}
