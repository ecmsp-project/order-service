package com.ecmsp.orderservice.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderFacade {

    List<Order> getAllOrders();

    Optional<Order> findOrderById(OrderId orderId);

    //Context can be added if needed for tracing/logging
    OrderCreated createOrder(OrderToCreate orderToCreate);

    Order updateOrder(OrderToUpdate orderToUpdate);

    void deleteOrder(OrderId orderId);

    boolean canOrderBeReturned(OrderId orderId);

    List<OrderItem> getReturnableItems(OrderId orderId);

    List<Order> getOrdersByClientId(ClientId clientId);

}
