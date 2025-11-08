package com.ecmsp.orderservice.api.grpc;

import com.ecmsp.order.v1.*;
import com.ecmsp.order.v1.OrderServiceGrpc;
import com.ecmsp.orderservice.application.security.grpc.UserContextGrpcHolder;
import com.ecmsp.orderservice.application.security.UserContextData;
import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.Order;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@GrpcService
class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {
    private final OrderFacade orderFacade;
    private final OrderGrpcMapper orderGrpcMapper;

    public OrderGrpcService(OrderFacade orderFacade, OrderGrpcMapper orderGrpcMapper) {
        this.orderFacade = orderFacade;
        this.orderGrpcMapper = orderGrpcMapper;
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<GetOrderResponse> responseObserver) {
        try {
            UUID oderId = UUID.fromString(request.getOrderId());
            Optional<Order> order = orderFacade.findOrderById(new OrderId(oderId));
            if (order.isPresent()) {
                GetOrderResponse response = orderGrpcMapper.toGetOrderResponse(order.get());
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
    public void getOrderStatus(GetOrderStatusRequest request, StreamObserver<GetOrderStatusResponse> responseObserver) {
        try {
            UUID orderId = UUID.fromString(request.getOrderId());
            Optional<Order> order = orderFacade.findOrderById(new OrderId(orderId));
            if (order.isPresent()) {
                GetOrderStatusResponse response = orderGrpcMapper.toGetOrderStatusResponse(order.get());
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
    public void getOrderItems(GetOrderItemsRequest request, StreamObserver<GetOrderItemsResponse> responseObserver) {
        try {
            UUID orderId = UUID.fromString(request.getOrderId());
            Optional<Order> order = orderFacade.findOrderById(new OrderId(orderId));
            if (order.isPresent()) {
                GetOrderItemsResponse response = orderGrpcMapper.toGetOrderItemsResponse(order.get());
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


    @Override
    public void listOrdersByUserId(ListOrdersByUserIdRequest request, StreamObserver<ListOrdersByUserIdResponse> responseObserver) {
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();

            var orders = orderFacade.getOrdersByClientId(new ClientId(UUID.fromString(userContextData.userId())));
            var response = ListOrdersByUserIdResponse.newBuilder()
                    .addAllOrders(orders.stream().map(orderGrpcMapper::toOrderResponse).toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();
            ClientId clientId = new ClientId(UUID.fromString(userContextData.userId()));

            OrderToCreate orderToCreate = orderGrpcMapper.toOrderToCreate(clientId, request);
            OrderCreated orderCreated = orderFacade.createOrder(orderToCreate);

            CreateOrderResponse createOrderResponse = orderGrpcMapper.toCreateOrderResponse(orderCreated);

            responseObserver.onNext(createOrderResponse);
            responseObserver.onCompleted();


        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
