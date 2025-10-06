package com.ecmsp.orderservice.api.grpc;

import com.ecmsp.order.v1.*;
import com.ecmsp.order.v1.returns.v1.*;
import com.ecmsp.orderservice.application.security.UserContextData;
import com.ecmsp.orderservice.application.security.grpc.UserContextGrpcHolder;
import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.returns.ReturnFacade;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnOrder;
import com.ecmsp.orderservice.order.domain.returns.ReturnToCreate;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GrpcService
public class ReturnGrpcService extends ReturnServiceGrpc.ReturnServiceImplBase {

    private final ReturnFacade returnFacade;
    private final ReturnGrpcMapper returnGrpcMapper;

    public ReturnGrpcService(ReturnFacade returnFacade, ReturnGrpcMapper returnGrpcMapper) {
        this.returnFacade = returnFacade;
        this.returnGrpcMapper = returnGrpcMapper;
    }

    @Override
    public void createReturn(CreateReturnRequest request, StreamObserver<CreateReturnResponse> responseObserver) {
        try {
            ReturnToCreate returnToCreate = returnGrpcMapper.toReturnToCreate(request);
            ReturnOrder createdReturn = returnFacade.createReturn(returnToCreate);
            CreateReturnResponse response = returnGrpcMapper.toCreateReturnResponse(createdReturn);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (OrderException.NotFound e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (OrderException.OrderNotReturnable e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getReturn(GetReturnRequest request, StreamObserver<GetReturnResponse> responseObserver) {
        try {
            UUID returnId = UUID.fromString(request.getReturnId());
            Optional<ReturnOrder> returnOrder = returnFacade.findReturnById(new ReturnId(returnId));
            if (returnOrder.isPresent()) {
                GetReturnResponse response = returnGrpcMapper.toGetReturnResponse(returnOrder.get());
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Return not found").asRuntimeException());
            }
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void listReturns(ListReturnsRequest request, StreamObserver<ListReturnsResponse> responseObserver) {
        try {
            List<ReturnOrder> returns = returnFacade.getAllReturns();
            List<com.ecmsp.order.v1.returns.v1.Return> returnResponses = returns.stream()
                    .map(returnGrpcMapper::toReturn)
                    .toList();

            ListReturnsResponse response = ListReturnsResponse.newBuilder()
                    .addAllReturns(returnResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void listReturnsByUserId(ListReturnsByUserIdRequest request, StreamObserver<ListReturnsByUserIdResponse> responseObserver) {
        try {
            UserContextData userContextData = UserContextGrpcHolder.getUserContext();
            ClientId clientId = new ClientId(UUID.fromString(userContextData.userId()));

            List<ReturnOrder> returns = returnFacade.getReturnsByUserId(clientId);
            List<com.ecmsp.order.v1.returns.v1.Return> returnResponses = returns.stream()
                    .map(returnGrpcMapper::toReturn)
                    .toList();

            ListReturnsByUserIdResponse response = ListReturnsByUserIdResponse.newBuilder()
                    .addAllReturns(returnResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
