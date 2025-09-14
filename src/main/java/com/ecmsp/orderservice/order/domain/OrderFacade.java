package com.ecmsp.orderservice.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderFacade {

    List<Order> getAllOrders();

    Optional<Order> findOrderById(OrderId orderId);

    Order createOrder(OrderToCreate orderToCreate, Context context);

    Order updateOrder(OrderToUpdate orderToUpdate);

    void deleteOrder(OrderId orderId);

    boolean canOrderBeReturned(OrderId orderId);

    List<OrderItem> getReturnableItems(OrderId orderId);

}
