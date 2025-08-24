package com.ecmsp.orderservice.grpc;
import com.ecmsp.orderservice.grpc.OrderServiceProto.OrderResponse;
import com.ecmsp.orderservice.grpc.OrderServiceProto.OrderItemDetails;
import com.ecmsp.orderservice.grpc.OrderStatusProto;
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



    public OrderStatusProto.OrderStatus toOrderStatusProto(OrderStatus orderStatusDomain) {
        return switch (orderStatusDomain) {
            case PENDING -> OrderStatusProto.OrderStatus.ORDER_STATUS_PENDING;
            case PROCESSING -> OrderStatusProto.OrderStatus.ORDER_STATUS_PROCESSING;
            case PAID -> OrderStatusProto.OrderStatus.ORDER_STATUS_PAID;
            case FAILED -> OrderStatusProto.OrderStatus.ORDER_STATUS_FAILED;
            case CANCELLED -> OrderStatusProto.OrderStatus.ORDER_STATUS_CANCELLED;
            default -> OrderStatusProto.OrderStatus.ORDER_STATUS_UNSPECIFIED;
        };
    }


    public OrderStatus toOrderStatusDomain(OrderStatusProto.OrderStatus orderStatusProto) {
        return switch (orderStatusProto) {
            case ORDER_STATUS_PENDING -> PENDING;
            case ORDER_STATUS_PROCESSING -> PROCESSING;
            case ORDER_STATUS_PAID -> PAID;
            case ORDER_STATUS_FAILED -> FAILED;
            case ORDER_STATUS_CANCELLED -> CANCELLED;
            default -> UNSPECIFIED;
        };
    }


    public OrderToCreate toOrderToCreate(com.ecmsp.orderservice.grpc.OrderServiceProto.CreateOrderRequest request) {
        return new OrderToCreate(
                new ClientId(UUID.fromString(request.getClientId())),
                request.getItemsList().stream()
                        .map(this::toOrderItem)
                        .toList()
        );
    }

    public OrderToUpdate toOrderToUpdate(com.ecmsp.orderservice.grpc.OrderServiceProto.UpdateOrderRequest request) {
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

    private OrderItem toOrderItem(com.ecmsp.orderservice.grpc.OrderServiceProto.OrderItem item) {
        return new OrderItem(
                new ItemId(UUID.fromString(item.getItemId())),
                item.getQuantity(),
                BigDecimal.valueOf(item.getPrice())
        );
    }

}