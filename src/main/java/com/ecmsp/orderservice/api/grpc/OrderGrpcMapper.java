package com.ecmsp.orderservice.api.grpc;
import com.ecmsp.order.v1.*;
import com.ecmsp.orderservice.order.domain.OrderItem;
import com.ecmsp.orderservice.order.domain.OrderStatus;

import com.ecmsp.orderservice.order.domain.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.ecmsp.orderservice.order.domain.OrderStatus.*;


@Component
public class OrderGrpcMapper {

    public GetOrderResponse toOrderResponse(Order order) {
        List<OrderItemDetails> itemDetails = order.items().stream()
                .map(this::toOrderItemDetails)
                .toList();

        return GetOrderResponse.newBuilder()
                .setOrderId(order.orderId().toString())
                .setOrderStatus(toOrderStatusProto(order.orderStatus()))
                .setDate(order.date().toString())
                .addAllItems(itemDetails)
                .build();
    }

    public GetOrderResponse toGetOrderResponse(Order order) {
        return toOrderResponse(order);
    }



    public CreateOrderResponse toCreateOrderResponse(Order order) {
        List<OrderItemDetails> itemDetails = order.items().stream()
                .map(this::toOrderItemDetails)
                .toList();

        return CreateOrderResponse.newBuilder()
                .setOrderId(order.orderId().toString())
                .setClientId(order.clientId().toString())
                .setOrderStatus(toOrderStatusProto(order.orderStatus()))
                .setDate(order.date().toString())
                .addAllItems(itemDetails)
                .build();
    }


    public UpdateOrderResponse toUpdateOrderResponse(Order order) {
        List<OrderItemDetails> itemDetails = order.items().stream()
                .map(this::toOrderItemDetails)
                .toList();

        return UpdateOrderResponse.newBuilder()
                .setOrderId(order.orderId().toString())
                .setClientId(order.clientId().toString())
                .setOrderStatus(toOrderStatusProto(order.orderStatus()))
                .setDate(order.date().toString())
                .addAllItems(itemDetails)
                .build();
    }


    public com.ecmsp.order.v1.OrderStatus toOrderStatusProto(OrderStatus orderStatusDomain) {
        return switch (orderStatusDomain) {
            case PENDING -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_PENDING;
            case PROCESSING -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_PROCESSING;
            case PAID -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_PAID;
            case FAILED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_FAILED;
            case CANCELLED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_CANCELLED;
            default -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_UNSPECIFIED;
        };
    }


    public OrderStatus toOrderStatusDomain(com.ecmsp.order.v1.OrderStatus orderStatusProto) {
        return switch (orderStatusProto) {
            case ORDER_STATUS_PENDING -> PENDING;
            case ORDER_STATUS_PROCESSING -> PROCESSING;
            case ORDER_STATUS_PAID -> PAID;
            case ORDER_STATUS_FAILED -> FAILED;
            case ORDER_STATUS_CANCELLED -> CANCELLED;
            default -> UNSPECIFIED;
        };
    }


    public OrderToCreate toOrderToCreate(CreateOrderRequest request) {
        return new OrderToCreate(
                new ClientId(UUID.fromString(request.getClientId())),
                request.getItemsList().stream()
                        .map(this::toOrderItem)
                        .toList()
        );
    }

    public GetOrderStatusResponse toGetOrderStatusResponse(Order order) {
        return GetOrderStatusResponse.newBuilder()
                .setOrderId(order.orderId().toString())
                .setOrderStatus(toOrderStatusProto(order.orderStatus()))
                .build();
    }

    public GetOrderItemsResponse toGetOrderItemsResponse(Order order) {
        List<OrderItemDetails> itemDetails = order.items().stream()
                .map(this::toOrderItemDetails)
                .toList();

        return GetOrderItemsResponse.newBuilder()
                .addAllItems(itemDetails)
                .build();
    }

    //TODO: COMPATITLE WITH SCHEMA DEFINITIONS BUT NOT THIS SERVICE DOMAIN
    private OrderItemDetails toOrderItemDetails(OrderItem item) {
        return OrderItemDetails.newBuilder()
                .setItemId(item.itemId().toString())
                .setQuantity(item.quantity())
                .build();
    }

    private OrderItem toOrderItem(com.ecmsp.order.v1.OrderItem item) {
        return new OrderItem(
                new ItemId(UUID.fromString(item.getItemId())),
                item.getQuantity(),
                BigDecimal.valueOf(item.getPrice()),
                //TODO: variant_id should be added
                true // Default to returnable for items created via gRPC
        );
    }

}