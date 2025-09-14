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

    public OrderResponse toOrderResponse(Order order) {
        List<OrderItemDetails> itemDetails = order.items().stream()
                .map(this::toOrderItemDetails)
                .toList();

        return OrderResponse.newBuilder()
                .setOrderId(order.orderId().toString())
                .setClientId(order.clientId().toString())
                .setOrderStatus(toOrderStatusProto(order.orderStatus()))
                .setDate(order.date().toString())
                .addAllItems(itemDetails)
                .build();
    }

    public GetOrderResponse toGetOrderResponse(Order order) {
        OrderResponse baseResponse = toOrderResponse(order);

        return GetOrderResponse.newBuilder()
                .setOrderId(baseResponse.getOrderId())
                .setClientId(baseResponse.getClientId())
                .setOrderStatus(baseResponse.getOrderStatus())
                .setDate(baseResponse.getDate())
                .addAllItems(baseResponse.getItemsList())
                .build();
    }



    public CreateOrderResponse toCreateOrderResponse(Order order) {
        OrderResponse baseResponse = toOrderResponse(order);

        return CreateOrderResponse.newBuilder()
                .setOrderId(baseResponse.getOrderId())
                .setClientId(baseResponse.getClientId())
                .setOrderStatus(baseResponse.getOrderStatus())
                .setDate(baseResponse.getDate())
                .addAllItems(baseResponse.getItemsList())
                .build();
    }


    public UpdateOrderResponse toUpdateOrderResponse(Order order) {
        OrderResponse baseResponse = toOrderResponse(order);

        return UpdateOrderResponse.newBuilder()
                .setOrderId(baseResponse.getOrderId())
                .setClientId(baseResponse.getClientId())
                .setOrderStatus(baseResponse.getOrderStatus())
                .setDate(baseResponse.getDate())
                .addAllItems(baseResponse.getItemsList())
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

    public OrderToUpdate toOrderToUpdate(UpdateOrderRequest request) {
        return new OrderToUpdate(
                new OrderId(UUID.fromString(request.getOrderId())),
                toOrderStatusDomain(request.getOrderStatus())
        );
    }

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
                true // Default to returnable for items created via gRPC
        );
    }

}