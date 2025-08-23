package com.ecmsp.orderservice.order.adapter.repository.db;

import com.ecmsp.orderservice.order.domain.*;

class OrderEntityMapper {

    public Order toOrder(OrderEntity orderEntity) {
        return new Order(
            /* orderId = */ new OrderId(orderEntity.getOrderId()),
            /* clientId = */ new ClientId(orderEntity.getClientId()),
            /* orderStatus = */ orderEntity.getOrderStatus(),
            /* date = */ orderEntity.getDate(),
            /* items = */ orderEntity.getItems().stream()
            .map(item -> new OrderItem( new ItemId(item.getItemId()), item.getQuantity(), item.getPrice()))
            .toList()
        );
    }

    public OrderEntity toOrderEntity(Order order) {
        return OrderEntity.builder()
            .orderId(order.orderId().value())
            .clientId(order.clientId().value())
            .orderStatus(order.orderStatus())
            .date(order.date())
            .items(order.items().stream()
                .map(item -> OrderItemEntity.builder()
                    .itemId(item.itemId().value())
                    .quantity(item.quantity())
                    .build())
                .toList())
            .build();
    }

}
