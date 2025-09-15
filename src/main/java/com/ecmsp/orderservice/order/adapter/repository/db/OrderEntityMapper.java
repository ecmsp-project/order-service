package com.ecmsp.orderservice.order.adapter.repository.db;

import com.ecmsp.orderservice.order.domain.*;

import java.util.List;

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
        OrderEntity orderEntity = OrderEntity.builder()
            .orderId(order.orderId().value())
            .clientId(order.clientId().value())
            .orderStatus(order.orderStatus())
            .date(order.date())
            .build();

        List<OrderItemEntity> itemEntities = order.items().stream()
            .map(item -> OrderItemEntity.builder()
                .itemId(item.itemId().value())
                .quantity(item.quantity())
                .price(item.priceAtTimeOfOrder())
                .build())
            .toList();

        itemEntities.forEach(itemEntity -> itemEntity.setOrder(orderEntity));
        orderEntity.setItems(itemEntities);

        return orderEntity;
    }

}
