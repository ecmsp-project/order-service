package com.ecmsp.orderservice.order.adapter.reservation.grpc;

import com.ecmsp.orderservice.order.domain.reservation.ReservationClient;
import com.ecmsp.orderservice.order.domain.reservation.ReservationCreated;
import com.ecmsp.orderservice.order.domain.reservation.ReservationToCreate;
import com.ecmsp.product.v1.reservation.v1.CreateVariantsReservationRequest;
import com.ecmsp.product.v1.reservation.v1.CreateVariantsReservationResponse;
import com.ecmsp.product.v1.reservation.v1.VariantReservationServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;


class ReservationGrpcClient implements ReservationClient {

    //TODO: change to reservation service ?
    @GrpcClient("product-service")
    private VariantReservationServiceGrpc.VariantReservationServiceBlockingStub variantReservationServiceStub;
    private final ReservationGrpcMapper reservationGrpcMapper;

    public ReservationGrpcClient(ReservationGrpcMapper reservationGrpcMapper) {
        this.reservationGrpcMapper = reservationGrpcMapper;
    }

    @Override
    public ReservationCreated createReservation(ReservationToCreate reservationToCreate) {
        System.out.println("Calling gRPC to create reservation for orderId: " + reservationToCreate.orderId().value());
        CreateVariantsReservationRequest request = reservationGrpcMapper.toCreateReservationRequest(reservationToCreate);
        CreateVariantsReservationResponse reservationResponse = variantReservationServiceStub.createVariantsReservation(request);
        return reservationGrpcMapper.toReservationCreated(reservationResponse);

    }




}
