package com.ecmsp.orderservice.order.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class TestOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders;

    public TestOrderRepository(List<Order> orders) {
        this.orders = orders.stream()
            .collect(Collectors.toMap(Order::orderId, order -> order));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public void create(Order order) {
        if (orders.containsKey(order.orderId())) {
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
}
