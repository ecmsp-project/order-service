package com.ecmsp.orderservice.api.grpc;

import com.ecmsp.order.v1.GetOrderItemsResponse;
import com.ecmsp.order.v1.GetOrderResponse;
import com.ecmsp.order.v1.GetOrderStatusResponse;
import com.ecmsp.order.v1.OrderItemDetails;
import com.ecmsp.orderservice.order.domain.Order;
import com.ecmsp.orderservice.order.domain.OrderItem;
import com.ecmsp.orderservice.order.domain.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecmsp.orderservice.order.domain.OrderStatus.*;


@Component
public class OrderGrpcMapper {

    public GetOrderResponse toOrderResponse(Order order) {
        List<OrderItemDetails> itemDetails = order.items().stream()
                .map(this::toOrderItemDetails)
                .toList();

        GetOrderResponse.Builder builder = GetOrderResponse.newBuilder()
                .setOrderId(order.orderId().toString())
                .setOrderStatus(toOrderStatusProto(order.orderStatus()))
                .setDate(order.date().toString())
                .addAllItems(itemDetails)
                .setClientId(order.clientId().toString());

        return builder.build();
    }

    public GetOrderResponse toGetOrderResponse(Order order) {
        return toOrderResponse(order);
    }





    public com.ecmsp.order.v1.OrderStatus toOrderStatusProto(OrderStatus orderStatusDomain) {
        return switch (orderStatusDomain) {
            case PENDING -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_PENDING;
            case PROCESSING -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_PROCESSING;
            case PAID -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_PAID;
            case FAILED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_FAILED;
            case CANCELLED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_CANCELLED;
            case SHIPPED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_SHIPPED;
            case DELIVERED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_DELIVERED;
            case RETURN_REQUESTED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_RETURN_REQUESTED;
            case RETURN_PROCESSING -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_RETURN_PROCESSING;
            case RETURNED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_RETURNED;
            case UNSPECIFIED -> com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_UNSPECIFIED;
        };
    }


    public OrderStatus toOrderStatusDomain(com.ecmsp.order.v1.OrderStatus orderStatusProto) {
        return switch (orderStatusProto) {
            case ORDER_STATUS_PENDING -> PENDING;
            case ORDER_STATUS_PROCESSING -> PROCESSING;
            case ORDER_STATUS_PAID -> PAID;
            case ORDER_STATUS_FAILED -> FAILED;
            case ORDER_STATUS_CANCELLED -> CANCELLED;
            case ORDER_STATUS_SHIPPED -> OrderStatus.SHIPPED;
            case ORDER_STATUS_DELIVERED -> OrderStatus.DELIVERED;
            case ORDER_STATUS_RETURN_REQUESTED -> OrderStatus.RETURN_REQUESTED;
            case ORDER_STATUS_RETURN_PROCESSING -> OrderStatus.RETURN_PROCESSING;
            case ORDER_STATUS_RETURNED -> OrderStatus.RETURNED;
            case ORDER_STATUS_UNSPECIFIED, UNRECOGNIZED -> UNSPECIFIED;
        };
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

    private OrderItemDetails toOrderItemDetails(OrderItem item) {
        return OrderItemDetails.newBuilder()
                .setItemId(item.itemId().toString())
                .setVariantId(item.variantId().toString())
                .setQuantity(item.quantity())
                .setPrice(item.price().doubleValue())
                .setIsReturnable(item.isReturnable())
                .setImageUrl(item.imageUrl())
                .setVariantId(item.variantId().toString())
                .build();


    }


}