package com.ecmsp.orderservice.grpc;
import com.ecmsp.orderservice.grpc.OrderGrpcMapper;
import com.ecmsp.orderservice.grpc.OrderServiceProto.*;
import com.ecmsp.orderservice.order.domain.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@GrpcService
public class OrderGrpcService extends com.ecmsp.orderservice.grpc.OrderServiceGrpc.OrderServiceImplBase {
    private final OrderFacade orderFacade;
    private final OrderGrpcMapper orderGrpcMapper;

    public OrderGrpcService(OrderFacade orderFacade, OrderGrpcMapper orderGrpcMapper) {
        this.orderFacade = orderFacade;
        this.orderGrpcMapper = orderGrpcMapper;
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            UUID oderId = UUID.fromString(request.getOrderId());
            Optional<Order> order = orderFacade.findOrderById(new OrderId(oderId));
            if (order.isPresent()) {
                OrderResponse response = orderGrpcMapper.toOrderResponse(order.get());
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Order not found").asRuntimeException());
            }
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            OrderToCreate orderToCreate = orderGrpcMapper.toOrderToCreate(request);
            Order createdOrder = orderFacade.createOrder(orderToCreate);
            OrderResponse response = orderGrpcMapper.toOrderResponse(createdOrder);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateOrder(UpdateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            OrderToUpdate orderToUpdate = orderGrpcMapper.toOrderToUpdate(request);
            Order updatedOrder = orderFacade.updateOrder(orderToUpdate);
            OrderResponse response = orderGrpcMapper.toOrderResponse(updatedOrder);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (OrderException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }

    }

    @Override
    public void deleteOrder(DeleteOrderRequest request, StreamObserver<DeleteOrderResponse> responseObserver) {
        try {
            UUID orderId = UUID.fromString(request.getOrderId());
            orderFacade.deleteOrder(new OrderId(orderId));
            DeleteOrderResponse response = DeleteOrderResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void listOrders(ListOrdersRequest request, StreamObserver<ListOrdersResponse> responseObserver) {
        try {
            var orders = orderFacade.getAllOrders();
            var response = ListOrdersResponse.newBuilder()
                    .addAllOrders(orders.stream().map(orderGrpcMapper::toOrderResponse).toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
