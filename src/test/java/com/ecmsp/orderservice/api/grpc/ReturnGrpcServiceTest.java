package com.ecmsp.orderservice.api.grpc;

import com.ecmsp.order.v1.returns.v1.*;
import com.ecmsp.orderservice.api.grpc.ReturnGrpcMapper;
import com.ecmsp.orderservice.api.grpc.ReturnGrpcService;
import com.ecmsp.orderservice.application.security.UserContextData;
import com.ecmsp.orderservice.application.security.grpc.UserContextGrpcHolder;
import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.returns.*;
import com.ecmsp.orderservice.order.domain.returns.ReturnStatus;
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
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReturnGrpcService Tests")
class ReturnGrpcServiceTest {

    private static final UUID RETURN_UUID = UUID.fromString("4845fd3b-62b1-40a1-ab32-57aa2ecf562f");
    private static final UUID ORDER_UUID = UUID.fromString("3745fd3b-62b1-40a1-ab32-57aa2ecf562f");
    private static final UUID CLIENT_UUID = UUID.fromString("b5d1eec8-c3ea-4b55-8cec-900b5c018381");
    private static final UUID ITEM_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID VARIANT_UUID = UUID.fromString("b1b2c3d4-e5f6-7890-abcd-ef1234567890");

    private static final ReturnId RETURN_ID = new ReturnId(RETURN_UUID);
    private static final OrderId ORDER_ID = new OrderId(ORDER_UUID);
    private static final ClientId CLIENT_ID = new ClientId(CLIENT_UUID);
    private static final ItemId ITEM_ID = new ItemId(ITEM_UUID);
    private static final VariantId VARIANT_ID = new VariantId(VARIANT_UUID);

    @Mock
    private ReturnFacade returnFacade;

    @Mock
    private ReturnGrpcMapper returnGrpcMapper;

    @InjectMocks
    private ReturnGrpcService returnGrpcService;

    private ReturnOrder testReturn;
    private ItemToReturnDetails testItem;

    @BeforeEach
    void setUp() {
        testItem = new ItemToReturnDetails(ITEM_ID, VARIANT_ID, 1, "Defective item");
        testReturn = new ReturnOrder(
            RETURN_ID,
            ORDER_ID,
            List.of(testItem),
            ReturnStatus.REQUESTED,
            LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Create Return Tests")
    class CreateReturnTests {

        private final StreamObserver<CreateReturnResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should create return successfully")
        void should_create_return_successfully() throws OrderException.OrderNotReturnable {
            // given
            CreateReturnRequest request = CreateReturnRequest.newBuilder()
                .setOrderId(ORDER_UUID.toString())
                .build();
            ReturnToCreate returnToCreate = new ReturnToCreate(ORDER_ID, List.of(testItem));
            CreateReturnResponse expectedResponse = CreateReturnResponse.newBuilder()
                .setReturnId(RETURN_UUID.toString())
                .setStatus(com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_REQUESTED)
                .build();

            when(returnGrpcMapper.toReturnToCreate(request)).thenReturn(returnToCreate);
            when(returnFacade.createReturn(returnToCreate)).thenReturn(testReturn);
            when(returnGrpcMapper.toCreateReturnResponse(testReturn)).thenReturn(expectedResponse);

            // when
            returnGrpcService.createReturn(request, responseObserver);

            // then
            verify(returnGrpcMapper).toReturnToCreate(request);
            verify(returnFacade).createReturn(returnToCreate);
            verify(returnGrpcMapper).toCreateReturnResponse(testReturn);
            verify(responseObserver).onNext(expectedResponse);
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should return NOT_FOUND when order does not exist")
        void should_return_not_found_when_order_does_not_exist() throws OrderException.OrderNotReturnable {
            // given
            CreateReturnRequest request = CreateReturnRequest.newBuilder()
                .setOrderId(ORDER_UUID.toString())
                .build();
            ReturnToCreate returnToCreate = new ReturnToCreate(ORDER_ID, List.of(testItem));

            when(returnGrpcMapper.toReturnToCreate(request)).thenReturn(returnToCreate);
            when(returnFacade.createReturn(returnToCreate)).thenThrow(new OrderException.NotFound(ORDER_ID));

            // when
            returnGrpcService.createReturn(request, responseObserver);

            // then
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.NOT_FOUND.getCode());
        }

        @Test
        @DisplayName("Should return FAILED_PRECONDITION when order is not returnable")
        void should_return_failed_precondition_when_order_not_returnable() throws OrderException.OrderNotReturnable {
            // given
            CreateReturnRequest request = CreateReturnRequest.newBuilder()
                .setOrderId(ORDER_UUID.toString())
                .build();
            ReturnToCreate returnToCreate = new ReturnToCreate(ORDER_ID, List.of(testItem));

            when(returnGrpcMapper.toReturnToCreate(request)).thenReturn(returnToCreate);
            doThrow(new OrderException.OrderNotReturnable(ORDER_ID)).when(returnFacade).createReturn(returnToCreate);

            // when
            returnGrpcService.createReturn(request, responseObserver);

            // then
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.FAILED_PRECONDITION.getCode());
        }

        @Test
        @DisplayName("Should return INTERNAL error when exception occurs")
        void should_return_internal_error_when_exception_occurs() throws OrderException.OrderNotReturnable {
            // given
            CreateReturnRequest request = CreateReturnRequest.newBuilder()
                .setOrderId(ORDER_UUID.toString())
                .build();
            ReturnToCreate returnToCreate = new ReturnToCreate(ORDER_ID, List.of(testItem));
            String errorMessage = "Database connection failed";

            when(returnGrpcMapper.toReturnToCreate(request)).thenReturn(returnToCreate);
            when(returnFacade.createReturn(returnToCreate)).thenThrow(new RuntimeException(errorMessage));

            // when
            returnGrpcService.createReturn(request, responseObserver);

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
    @DisplayName("Get Return Tests")
    class GetReturnTests {

        private final StreamObserver<GetReturnResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should return return details when it exists")
        void should_return_return_details_when_exists() {
            // given
            GetReturnResponse expectedResponse = GetReturnResponse.newBuilder()
                .setReturn(Return.newBuilder()
                    .setReturnId(RETURN_UUID.toString())
                    .setOrderId(ORDER_UUID.toString())
                    .setStatus(com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_REQUESTED)
                    .build())
                .build();
            GetReturnRequest request = GetReturnRequest.newBuilder()
                .setReturnId(RETURN_UUID.toString())
                .build();

            when(returnFacade.findReturnById(RETURN_ID)).thenReturn(Optional.of(testReturn));
            when(returnGrpcMapper.toGetReturnResponse(testReturn)).thenReturn(expectedResponse);

            // when
            returnGrpcService.getReturn(request, responseObserver);

            // then
            verify(returnFacade).findReturnById(RETURN_ID);
            verify(returnGrpcMapper).toGetReturnResponse(testReturn);
            verify(responseObserver).onNext(expectedResponse);
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should return NOT_FOUND when return does not exist")
        void should_return_not_found_when_return_does_not_exist() {
            // given
            GetReturnRequest request = GetReturnRequest.newBuilder()
                .setReturnId(RETURN_UUID.toString())
                .build();

            when(returnFacade.findReturnById(RETURN_ID)).thenReturn(Optional.empty());

            // when
            returnGrpcService.getReturn(request, responseObserver);

            // then
            verify(returnFacade).findReturnById(RETURN_ID);
            verify(returnGrpcMapper, never()).toGetReturnResponse(any());
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();

            ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
            verify(responseObserver).onError(exceptionCaptor.capture());

            StatusRuntimeException exception = exceptionCaptor.getValue();
            assertThat(exception.getStatus().getCode()).isEqualTo(Status.NOT_FOUND.getCode());
            assertThat(exception.getMessage()).contains("Return not found");
        }

        @Test
        @DisplayName("Should return INTERNAL error when exception occurs")
        void should_return_internal_error_when_exception_occurs() {
            // given
            GetReturnRequest request = GetReturnRequest.newBuilder()
                .setReturnId(RETURN_UUID.toString())
                .build();
            String errorMessage = "Database connection failed";

            when(returnFacade.findReturnById(RETURN_ID)).thenThrow(new RuntimeException(errorMessage));

            // when
            returnGrpcService.getReturn(request, responseObserver);

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
    @DisplayName("List Returns Tests")
    class ListReturnsTests {

        private final StreamObserver<ListReturnsResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should list all returns successfully")
        void should_list_all_returns_successfully() {
            // given
            List<ReturnOrder> returns = List.of(testReturn);
            Return returnProto = Return.newBuilder()
                .setReturnId(RETURN_UUID.toString())
                .setOrderId(ORDER_UUID.toString())
                .setStatus(com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_REQUESTED)
                .build();
            ListReturnsRequest request = ListReturnsRequest.newBuilder().build();

            when(returnFacade.getAllReturns()).thenReturn(returns);
            when(returnGrpcMapper.toReturn(testReturn)).thenReturn(returnProto);

            // when
            returnGrpcService.listReturns(request, responseObserver);

            // then
            verify(returnFacade).getAllReturns();
            verify(returnGrpcMapper).toReturn(testReturn);
            verify(responseObserver).onNext(argThat(response ->
                response.getReturnsList().size() == 1 &&
                response.getReturnsList().getFirst().getReturnId().equals(RETURN_UUID.toString())
            ));
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should handle empty return list")
        void should_handle_empty_return_list() {
            // given
            List<ReturnOrder> emptyReturns = List.of();
            ListReturnsRequest request = ListReturnsRequest.newBuilder().build();

            when(returnFacade.getAllReturns()).thenReturn(emptyReturns);

            // when
            returnGrpcService.listReturns(request, responseObserver);

            // then
            verify(returnFacade).getAllReturns();
            verify(returnGrpcMapper, never()).toReturn(any());
            verify(responseObserver).onNext(argThat(response -> response.getReturnsList().isEmpty()));
            verify(responseObserver).onCompleted();
            verify(responseObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should handle exception during return listing")
        void should_handle_exception_during_return_listing() {
            // given
            ListReturnsRequest request = ListReturnsRequest.newBuilder().build();

            when(returnFacade.getAllReturns()).thenThrow(new RuntimeException("DB error"));

            // when
            returnGrpcService.listReturns(request, responseObserver);

            // then
            verify(responseObserver).onError(any(StatusRuntimeException.class));
            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        }
    }

    @Nested
    @DisplayName("List Returns By User ID Tests")
    class ListReturnsByUserIdTests {

        private final StreamObserver<ListReturnsByUserIdResponse> responseObserver = mock(StreamObserver.class);

        @Test
        @DisplayName("Should list returns by user ID successfully")
        void should_list_returns_by_user_id_successfully() {
            try (MockedStatic<UserContextGrpcHolder> mockedStatic = mockStatic(UserContextGrpcHolder.class)) {
                // given
                UserContextData userContext = new UserContextData(CLIENT_UUID.toString(), "test@example.com");
                List<ReturnOrder> returns = List.of(testReturn);
                Return returnProto = Return.newBuilder()
                    .setReturnId(RETURN_UUID.toString())
                    .setOrderId(ORDER_UUID.toString())
                    .setStatus(com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_REQUESTED)
                    .build();
                ListReturnsByUserIdRequest request = ListReturnsByUserIdRequest.newBuilder().build();

                mockedStatic.when(UserContextGrpcHolder::getUserContext).thenReturn(userContext);
                when(returnFacade.getReturnsByUserId(CLIENT_ID)).thenReturn(returns);
                when(returnGrpcMapper.toReturn(testReturn)).thenReturn(returnProto);

                // when
                returnGrpcService.listReturnsByUserId(request, responseObserver);

                // then
                verify(returnFacade).getReturnsByUserId(CLIENT_ID);
                verify(returnGrpcMapper).toReturn(testReturn);
                verify(responseObserver).onNext(argThat(response ->
                    response.getReturnsList().size() == 1 &&
                    response.getReturnsList().getFirst().getReturnId().equals(RETURN_UUID.toString())
                ));
                verify(responseObserver).onCompleted();
                verify(responseObserver, never()).onError(any());
            }
        }

        @Test
        @DisplayName("Should handle empty return list for user")
        void should_handle_empty_return_list_for_user() {
            try (MockedStatic<UserContextGrpcHolder> mockedStatic = mockStatic(UserContextGrpcHolder.class)) {
                // given
                UserContextData userContext = new UserContextData(CLIENT_UUID.toString(), "test@example.com");
                List<ReturnOrder> emptyReturns = List.of();
                ListReturnsByUserIdRequest request = ListReturnsByUserIdRequest.newBuilder().build();

                mockedStatic.when(UserContextGrpcHolder::getUserContext).thenReturn(userContext);
                when(returnFacade.getReturnsByUserId(CLIENT_ID)).thenReturn(emptyReturns);

                // when
                returnGrpcService.listReturnsByUserId(request, responseObserver);

                // then
                verify(returnFacade).getReturnsByUserId(CLIENT_ID);
                verify(returnGrpcMapper, never()).toReturn(any());
                verify(responseObserver).onNext(argThat(response -> response.getReturnsList().isEmpty()));
                verify(responseObserver).onCompleted();
                verify(responseObserver, never()).onError(any());
            }
        }

        @Test
        @DisplayName("Should handle exception during user returns listing")
        void should_handle_exception_during_user_returns_listing() {
            try (MockedStatic<UserContextGrpcHolder> mockedStatic = mockStatic(UserContextGrpcHolder.class)) {
                // given
                UserContextData userContext = new UserContextData(CLIENT_UUID.toString(), "test@example.com");
                ListReturnsByUserIdRequest request = ListReturnsByUserIdRequest.newBuilder().build();

                mockedStatic.when(UserContextGrpcHolder::getUserContext).thenReturn(userContext);
                when(returnFacade.getReturnsByUserId(CLIENT_ID)).thenThrow(new RuntimeException("DB error"));

                // when
                returnGrpcService.listReturnsByUserId(request, responseObserver);

                // then
                verify(responseObserver).onError(any(StatusRuntimeException.class));
                verify(responseObserver, never()).onNext(any());
                verify(responseObserver, never()).onCompleted();
            }
        }
    }
}
