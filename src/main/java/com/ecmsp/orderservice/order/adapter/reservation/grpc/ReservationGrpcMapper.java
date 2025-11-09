package com.ecmsp.orderservice.order.adapter.reservation.grpc;

import com.ecmsp.orderservice.order.domain.VariantId;
import com.ecmsp.orderservice.order.domain.reservation.ReservationCreated;
import com.ecmsp.orderservice.order.domain.reservation.ReservationToCreate;
import com.ecmsp.product.v1.reservation.v1.CreateVariantsReservationRequest;
import com.ecmsp.product.v1.reservation.v1.CreateVariantsReservationResponse;
import com.ecmsp.product.v1.reservation.v1.FailedReservationVariant;
import com.ecmsp.product.v1.reservation.v1.ReservedVariant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class ReservationGrpcMapper {

    public CreateVariantsReservationRequest toCreateReservationRequest(ReservationToCreate reservationToCreate) {
        List<ReservedVariant> reservedVariants = reservationToCreate.variantsToReserve().stream()
                .map(this::toReservedVariant)
                .toList();

        return CreateVariantsReservationRequest.newBuilder()
                .setOrderId(reservationToCreate.orderId().value().toString())
                .addAllItems(reservedVariants)
                .build();
    }


    public ReservationCreated toReservationCreated(CreateVariantsReservationResponse response) {

        List<VariantId> variantIds = response.getReservedVariantIdsList().stream().map(
                variantId -> new VariantId(UUID.fromString(variantId))
        ).toList();

        List<ReservationCreated.VariantOutOfStock> failedVariants = response.getFailedVariantsList().stream()
                .map(this::toVariantOutOfStock)
                .toList();

        return new ReservationCreated(variantIds, failedVariants);

    }


    private ReservationCreated.VariantOutOfStock toVariantOutOfStock(FailedReservationVariant failedVariant) {
        return new ReservationCreated.VariantOutOfStock(
                new VariantId(UUID.fromString(failedVariant.getVariantId())),
                failedVariant.getRequestedQuantity(),
                failedVariant.getAvailableQuantity()
        );
    }


    private ReservedVariant toReservedVariant(ReservationToCreate.VariantToReserve variant) {
        return ReservedVariant.newBuilder()
                .setVariantId(variant.variantId().value().toString())
                .setQuantity(variant.quantity())
                .build();
    }


}
