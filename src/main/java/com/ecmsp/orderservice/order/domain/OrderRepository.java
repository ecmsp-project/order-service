package com.ecmsp.orderservice.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(OrderId orderId);

    void create(Order order);

    void update(Order order);

    void deleteById(OrderId orderId);

}
