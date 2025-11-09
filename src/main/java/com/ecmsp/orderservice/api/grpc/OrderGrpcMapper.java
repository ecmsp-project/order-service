package com.ecmsp.orderservice.api.grpc;

import com.ecmsp.order.v1.*;
import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.Order;
import com.ecmsp.orderservice.order.domain.OrderStatus;
import com.ecmsp.orderservice.order.domain.reservation.ReservationCreated;
import com.ecmsp.orderservice.order.domain.reservation.ReservationId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.ecmsp.orderservice.order.domain.OrderStatus.*;


@Component
class OrderGrpcMapper {

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


    public OrderToCreate toOrderToCreate(ClientId clientId, CreateOrderRequest createOrderRequest){
        System.out.println("Mapping CreateOrderRequest to OrderToCreate: " + createOrderRequest);

        List<OrderItem> itemsList = createOrderRequest.getItemsList().stream()
                .map(this::toOrderItem)
                .toList();

        OrderToCreate orderToCreate = new OrderToCreate(
                new ReservationId(UUID.randomUUID()), //TODO: should be removed it's only placeholder
                clientId,
                itemsList
        );

        System.out.println("Mapped OrderToCreate: " + orderToCreate);
        return orderToCreate;
    }


    public CreateOrderResponse toCreateOrderResponse(OrderCreated orderCreated) {
        return CreateOrderResponse.newBuilder()
                .setIsSuccess(orderCreated.isCreatedSuccessfully())
                .setOrderId(orderCreated.orderId() != null ? orderCreated.orderId().value().toString() : "")
                .addAllReservedVariantIds(
                        orderCreated.reservationCreated().reservedVariantIds().stream()
                                .map(variantId -> variantId.value().toString())
                                .toList()
                )
                .addAllFailedVariants(
                        orderCreated.reservationCreated().variantsOutOfStock().stream()
                                .map(this::toFailedReservationVariant)
                                .toList()
                )
                .build();
    }

    private FailedReservationVariant toFailedReservationVariant(ReservationCreated.VariantOutOfStock variantOutOfStock) {
        return FailedReservationVariant.newBuilder()
                .setVariantId(variantOutOfStock.variantId().value().toString())
                .setRequestedQuantity(variantOutOfStock.requestedQuantity())
                .setAvailableQuantity(variantOutOfStock.availableQuantity())
                .build();
    }



    private OrderItemDetails toOrderItemDetails(OrderItem item) {
        return OrderItemDetails.newBuilder()
                .setItemId(item.itemId().toString())
                .setVariantId(item.variantId().toString())
                .setName(item.name())
                .setQuantity(item.quantity())
                .setPrice(item.price().doubleValue())
                .setIsReturnable(item.isReturnable())
                .setImageUrl(item.imageUrl())
                .setVariantId(item.variantId().toString())
                .build();
    }

    private OrderItem toOrderItem(OrderItemDetails orderItemDetails){
        return new OrderItem(
                new ItemId(UUID.fromString(orderItemDetails.getItemId())),
                new VariantId(UUID.fromString(orderItemDetails.getVariantId())),
                orderItemDetails.getName(),
                orderItemDetails.getQuantity(),
                BigDecimal.valueOf(orderItemDetails.getPrice()),
                orderItemDetails.getImageUrl(),
                orderItemDetails.getDescription(),
                orderItemDetails.getIsReturnable()
        );
    }




}