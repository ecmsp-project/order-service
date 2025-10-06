package com.ecmsp.orderservice.api.grpc;

import com.ecmsp.order.v1.*;
import com.ecmsp.order.v1.OrderServiceGrpc;
import com.ecmsp.orderservice.application.security.grpc.UserContextGrpcHolder;
import com.ecmsp.orderservice.application.security.UserContextData;
import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.OrderStatus;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {
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

    //? NOT SURE IF THIS IS NEEDED
    @Override
    public void updateOrder(UpdateOrderRequest request, StreamObserver<UpdateOrderResponse> responseObserver) {
        try {
            UUID orderId = UUID.fromString(request.getOrderId());
            Optional<Order> order = orderFacade.findOrderById(new OrderId(orderId));

            if (order.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Order not found").asRuntimeException());
                return;
            }

            // Auto-advance order status based on current status
            OrderStatus currentStatus = order.get().orderStatus();
            OrderStatus nextStatus = switch (currentStatus) {
                case PENDING -> OrderStatus.PROCESSING;
                case PROCESSING -> OrderStatus.PAID;
//                case PAID -> OrderStatus.COMPLETED; COMPLETED status does not exist in our domain
                default -> currentStatus;
            };

            OrderToUpdate orderToUpdate = new OrderToUpdate(new OrderId(orderId), nextStatus);
            Order updatedOrder = orderFacade.updateOrder(orderToUpdate);
            UpdateOrderResponse response = orderGrpcMapper.toUpdateOrderResponse(updatedOrder);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (OrderException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }

    }



    /**
     * @deprecated This method is deprecated and will be removed in a future version.
     * Order creation should be done through the REST API or other designated endpoints.
     */
    @Deprecated
    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
        try {
            OrderToCreate orderToCreate = orderGrpcMapper.toOrderToCreate(request);
            Order createdOrder = orderFacade.createOrder(orderToCreate, null);
            CreateOrderResponse response = orderGrpcMapper.toCreateOrderResponse(createdOrder);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }




    /**
     * @deprecated This method is deprecated and will be removed in a future version.
     * Order deletion should be done through the REST API or other designated endpoints.
     */
    @Deprecated
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



}
