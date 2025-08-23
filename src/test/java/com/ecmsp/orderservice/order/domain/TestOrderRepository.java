package com.ecmsp.orderservice.order.domain;

import com.ecmsp.orderservice.order.domain.Order;
import com.ecmsp.orderservice.order.domain.OrderException;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderRepository;

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
}
