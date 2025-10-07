package com.ecmsp.orderservice.grpc;

import com.ecmsp.order.v1.*;
import com.ecmsp.orderservice.api.grpc.OrderGrpcMapper;
import com.ecmsp.orderservice.api.grpc.OrderGrpcService;
import com.ecmsp.orderservice.order.domain.ClientId;
import com.ecmsp.orderservice.order.domain.OrderFacade;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.Order;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderGrpcService Tests")
class OrderGrpcServiceTest {

    private static final UUID ORDER_UUID = UUID.fromString("3745fd3b-62b1-40a1-ab32-57aa2ecf562f");
    private static final UUID CLIENT_UUID = UUID.fromString("b5d1eec8-c3ea-4b55-8cec-900b5c018381");
    private static final OrderId ORDER_ID = new OrderId(ORDER_UUID);
    private static final ClientId CLIENT_ID = new ClientId(CLIENT_UUID);


    private final OrderFacade orderFacade = mock(OrderFacade.class);
    private final OrderGrpcMapper orderGrpcMapper = mock(OrderGrpcMapper.class);
    @InjectMocks
    private OrderGrpcService orderGrpcService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order(ORDER_ID, null, CLIENT_ID, null, null, null);
    }

    @Nested
    @DisplayName("Get Order Tests")
    class GetOrderTests {


        private final StreamObserver<GetOrderResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should return order when it exists")
        void should_return_order_when_exists() {
            // given
            GetOrderResponse expectedResponse = GetOrderResponse.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();
            GetOrderRequest request = GetOrderRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();

            when(orderFacade.findOrderById(ORDER_ID)).thenReturn(Optional.of(testOrder));
            when(orderGrpcMapper.toGetOrderResponse(testOrder)).thenReturn(expectedResponse);

            // when
            orderGrpcService.getOrder(request, responseObserver);

            // then
            verify(orderFacade).findOrderById(ORDER_ID);
            verify(orderGrpcMapper).toGetOrderResponse(testOrder);
            verify(responseObserver).onNext(expectedResponse);
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should return NOT_FOUND when order does not exist")
        void should_return_not_found_when_order_does_not_exist() {
            // given
            GetOrderRequest request = GetOrderRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();

            when(orderFacade.findOrderById(ORDER_ID)).thenReturn(Optional.empty());

            // when
            orderGrpcService.getOrder(request, responseObserver);

            // then
            verify(orderFacade).findOrderById(ORDER_ID);
            verify(orderGrpcMapper, never()).toGetOrderResponse(any());
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.NOT_FOUND.getCode());
            assertThat(exception.getMessage()).contains("Order not found");
        }

        @Test
        @DisplayName("Should return INTERNAL error when exception occurs")
        void should_return_internal_error_when_exception_occurs() {
            // given
            GetOrderRequest request = GetOrderRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();
            String errorMessage = "Database connection failed";

            when(orderFacade.findOrderById(ORDER_ID)).thenThrow(new RuntimeException(errorMessage));

            // when
            orderGrpcService.getOrder(request, responseObserver);

            // then
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.INTERNAL.getCode());
            assertThat(exception.getMessage()).contains(errorMessage);
        }
    }


    @Nested
    @DisplayName("Get Order Status Tests")
    class GetOrderStatusTests {

        private final StreamObserver<GetOrderStatusResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should return order status when order exists")
        void should_return_order_status_when_exists() {
            // given
            GetOrderStatusResponse expectedResponse = GetOrderStatusResponse.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .setOrderStatus(com.ecmsp.order.v1.OrderStatus.ORDER_STATUS_PENDING)
                    .build();
            GetOrderStatusRequest request = GetOrderStatusRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();

            when(orderFacade.findOrderById(ORDER_ID)).thenReturn(Optional.of(testOrder));
            when(orderGrpcMapper.toGetOrderStatusResponse(testOrder)).thenReturn(expectedResponse);

            // when
            orderGrpcService.getOrderStatus(request, responseObserver);

            // then
            verify(orderFacade).findOrderById(ORDER_ID);
            verify(orderGrpcMapper).toGetOrderStatusResponse(testOrder);
            verify(responseObserver).onNext(expectedResponse);
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should return NOT_FOUND when order does not exist")
        void should_return_not_found_when_order_does_not_exist() {
            // given
            GetOrderStatusRequest request = GetOrderStatusRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();

            when(orderFacade.findOrderById(ORDER_ID)).thenReturn(Optional.empty());

            // when
            orderGrpcService.getOrderStatus(request, responseObserver);

            // then
            verify(orderFacade).findOrderById(ORDER_ID);
            verify(orderGrpcMapper, never()).toGetOrderStatusResponse(any());
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.NOT_FOUND.getCode());
            assertThat(exception.getMessage()).contains("Order not found");
        }

        @Test
        @DisplayName("Should return INTERNAL error when exception occurs")
        void should_return_internal_error_when_exception_occurs() {
            // given
            GetOrderStatusRequest request = GetOrderStatusRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();
            String errorMessage = "Database connection failed";

            when(orderFacade.findOrderById(ORDER_ID)).thenThrow(new RuntimeException(errorMessage));

            // when
            orderGrpcService.getOrderStatus(request, responseObserver);

            // then
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.INTERNAL.getCode());
            assertThat(exception.getMessage()).contains(errorMessage);
        }
    }

    @Nested
    @DisplayName("Get Order Items Tests")
    class GetOrderItemsTests {

        private final StreamObserver<GetOrderItemsResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should return order items when order exists")
        void should_return_order_items_when_exists() {
            // given
            GetOrderItemsResponse expectedResponse = GetOrderItemsResponse.newBuilder()
                    .build();
            GetOrderItemsRequest request = GetOrderItemsRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();

            when(orderFacade.findOrderById(ORDER_ID)).thenReturn(Optional.of(testOrder));
            when(orderGrpcMapper.toGetOrderItemsResponse(testOrder)).thenReturn(expectedResponse);

            // when
            orderGrpcService.getOrderItems(request, responseObserver);

            // then
            verify(orderFacade).findOrderById(ORDER_ID);
            verify(orderGrpcMapper).toGetOrderItemsResponse(testOrder);
            verify(responseObserver).onNext(expectedResponse);
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should return NOT_FOUND when order does not exist")
        void should_return_not_found_when_order_does_not_exist() {
            // given
            GetOrderItemsRequest request = GetOrderItemsRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();

            when(orderFacade.findOrderById(ORDER_ID)).thenReturn(Optional.empty());

            // when
            orderGrpcService.getOrderItems(request, responseObserver);

            // then
            verify(orderFacade).findOrderById(ORDER_ID);
            verify(orderGrpcMapper, never()).toGetOrderItemsResponse(any());
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.NOT_FOUND.getCode());
            assertThat(exception.getMessage()).contains("Order not found");
        }

        @Test
        @DisplayName("Should return INTERNAL error when exception occurs")
        void should_return_internal_error_when_exception_occurs() {
            // given
            GetOrderItemsRequest request = GetOrderItemsRequest.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();
            String errorMessage = "Database connection failed";

            when(orderFacade.findOrderById(ORDER_ID)).thenThrow(new RuntimeException(errorMessage));

            // when
            orderGrpcService.getOrderItems(request, responseObserver);

            // then
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.INTERNAL.getCode());
            assertThat(exception.getMessage()).contains(errorMessage);
        }
    }

    @Nested
    @DisplayName("List Orders Tests")
    class ListOrdersTests {


        private final StreamObserver<ListOrdersResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should list all orders successfully")
        void should_list_all_orders_successfully() {
            // given
            List<Order> orders = List.of(testOrder);
            GetOrderResponse orderResponse = GetOrderResponse.newBuilder()
                    .setOrderId(ORDER_UUID.toString())
                    .build();
            ListOrdersRequest request = ListOrdersRequest.newBuilder().build();

            when(orderFacade.getAllOrders()).thenReturn(orders);
            when(orderGrpcMapper.toOrderResponse(testOrder)).thenReturn(orderResponse);

            // when
            orderGrpcService.listOrders(request, responseObserver);

            // then
            verify(orderFacade).getAllOrders();
            verify(orderGrpcMapper).toOrderResponse(testOrder);
            verify(responseObserver).onNext(argThat(response ->
                    response.getOrdersList().size() == 1 &&
                            response.getOrdersList().getFirst().getOrderId().equals(ORDER_UUID.toString())
            ));
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should handle empty order list")
        void should_handle_empty_order_list() {
            // given
            List<Order> emptyOrders = List.of();
            ListOrdersRequest request = ListOrdersRequest.newBuilder().build();

            when(orderFacade.getAllOrders()).thenReturn(emptyOrders);

            // when
            orderGrpcService.listOrders(request, responseObserver);

            // then
            verify(orderFacade).getAllOrders();
            verify(orderGrpcMapper, never()).toOrderResponse(any());
            verify(responseObserver).onNext(argThat(response -> response.getOrdersList().isEmpty()));
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should handle exception during order listing")
        void should_handle_exception_during_order_listing() {
            // given
            ListOrdersRequest request = ListOrdersRequest.newBuilder().build();

            when(orderFacade.getAllOrders()).thenThrow(new RuntimeException("DB error"));

            // when
            orderGrpcService.listOrders(request, responseObserver);

            // then
            verify(responseObserver).onError(any(StatusRuntimeException.class));
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        }
    }
}