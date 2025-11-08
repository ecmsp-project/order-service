package com.ecmsp.orderservice.order.adapter.repository.db;

import com.ecmsp.orderservice.order.domain.*;

import java.util.List;

class OrderEntityMapper {

    public Order toOrder(OrderEntity orderEntity) {
        return new Order(
            /* orderId = */ new OrderId(orderEntity.getOrderId()),
            /* reservationId = */ orderEntity.getReservationId() != null ? new ReservationId(orderEntity.getReservationId()) : null,
            /* clientId = */ new ClientId(orderEntity.getClientId()),
            /* orderStatus = */ orderEntity.getOrderStatus(),
            /* date = */ orderEntity.getDate(),
            /* items = */ orderEntity.getItems().stream()
            .map(item -> new OrderItem(
                new ItemId(item.getItemId()),
                item.getVariantId() != null ? new VariantId(item.getVariantId()) : null,
                item.getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getImageUrl(),
                item.getDescription(),
                item.getIsReturnable()
            ))
            .toList()
        );
    }

    public OrderEntity toOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
            .orderId(order.orderId().value())
            .reservationId(order.reservationId() != null ? order.reservationId().value() : null)
            .clientId(order.clientId().value())
            .orderStatus(order.orderStatus())
            .date(order.date())
            .build();

        List<OrderItemEntity> itemEntities = order.items().stream()
            .map(item -> OrderItemEntity.builder()
                .itemId(item.itemId().value())
                .variantId(item.variantId() != null ? item.variantId().value() : null)
                .quantity(item.quantity())
                .price(item.price())
                .imageUrl(item.imageUrl())
                .description(item.description())
                .isReturnable(item.isReturnable())
                .build())
            .toList();

        itemEntities.forEach(itemEntity -> itemEntity.setOrder(orderEntity));
        orderEntity.setItems(itemEntities);

        return orderEntity;
    }

}
