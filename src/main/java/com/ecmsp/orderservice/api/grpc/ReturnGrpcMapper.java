package com.ecmsp.orderservice.api.grpc;

import com.ecmsp.order.v1.returns.v1.*;
import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.domain.returns.ItemToReturnDetails;
import com.ecmsp.orderservice.order.domain.returns.ReturnOrder;
import com.ecmsp.orderservice.order.domain.returns.ReturnToCreate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ReturnGrpcMapper {

    public GetReturnResponse toGetReturnResponse(ReturnOrder returnOrder) {
        return GetReturnResponse.newBuilder()
                .setReturn(toReturn(returnOrder))
                .build();
    }

    public CreateReturnResponse toCreateReturnResponse(ReturnOrder returnOrder) {
        return CreateReturnResponse.newBuilder()
                .setReturnId(returnOrder.returnId().toString())
                .setStatus(toReturnStatusProto(returnOrder.status()))
                .build();
    }

    public ReturnToCreate toReturnToCreate(CreateReturnRequest request) {
        List<ItemToReturnDetails> itemsToReturn = request.getItemsList().stream()
                .map(item -> new ItemToReturnDetails(
                        new ItemId(UUID.fromString(item.getItemId())),
                        new VariantId(UUID.fromString(item.getVariantId())),
                        item.getQuantity(),
                        item.getReason()
                ))
                .toList();

        return new ReturnToCreate(
                new OrderId(UUID.fromString(request.getOrderId())),
                itemsToReturn
        );
    }

    public Return toReturn(ReturnOrder returnOrder) {

        List<ItemReturnDetails> items = returnOrder.itemsToReturn().stream()
                .map(this::toItemReturnDetails)
                .toList();

        return Return.newBuilder()
                .setReturnId(returnOrder.returnId().toString())
                .setOrderId(returnOrder.orderId().toString())
                .addAllItems(items)
                .setStatus(toReturnStatusProto(returnOrder.status()))
                .setCreatedAt(returnOrder.createdAt().toString())
                .build();
    }

    private ItemReturnDetails toItemReturnDetails(ItemToReturnDetails dto) {
        return ItemReturnDetails.newBuilder()
                .setItemId(dto.itemId().value().toString())
                .setVariantId(dto.variantId().value().toString())
                .setQuantity(dto.quantity())
                .setReason(dto.reason())
                .build();
    }


    private com.ecmsp.order.v1.returns.v1.ReturnStatus toReturnStatusProto(com.ecmsp.orderservice.order.domain.returns.ReturnStatus status) {
        return switch (status) {
            case REQUESTED -> com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_REQUESTED;
            case PROCESSING -> com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_PROCESSING;
            case COMPLETED -> com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_COMPLETED;
            case UNSPECIFIED -> com.ecmsp.order.v1.returns.v1.ReturnStatus.RETURN_STATUS_UNSPECIFIED;
        };
    }
}
