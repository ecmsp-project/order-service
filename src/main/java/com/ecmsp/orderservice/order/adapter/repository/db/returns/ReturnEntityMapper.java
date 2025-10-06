package com.ecmsp.orderservice.order.adapter.repository.db.returns;

import com.ecmsp.orderservice.order.domain.ItemId;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.VariantId;
import com.ecmsp.orderservice.order.domain.returns.ItemToReturnDetails;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnOrder;

import java.util.List;

class ReturnEntityMapper {

    public ReturnOrder toReturnOrder(ReturnEntity returnEntity) {
        return new ReturnOrder(
            /* returnId = */ new ReturnId(returnEntity.getReturnId()),
            /* orderId = */ new OrderId(returnEntity.getOrderId()),
            /* itemsToReturn = */ returnEntity.getItems().stream()
                .map(item -> new ItemToReturnDetails(
                    new ItemId(item.getItemId()),
                    new VariantId(item.getVariantId()),
                    item.getQuantity(),
                    item.getReason()
                ))
                .toList(),
            /* status = */ returnEntity.getStatus(),
            /* createdAt = */ returnEntity.getCreatedAt()
        );
    }

    public ReturnEntity toReturnEntity(ReturnOrder returnOrder) {
        ReturnEntity returnEntity = ReturnEntity.builder()
            .returnId(returnOrder.returnId().id())
            .orderId(returnOrder.orderId().value())
            .status(returnOrder.status())
            .createdAt(returnOrder.createdAt())
            .build();

        List<ReturnItemEntity> itemEntities = returnOrder.itemsToReturn().stream()
            .map(item -> ReturnItemEntity.builder()
                .itemId(item.itemId().value())
                .variantId(item.variantId().value())
                .quantity(item.quantity())
                .reason(item.reason())
                .build())
            .toList();

        itemEntities.forEach(itemEntity -> itemEntity.setReturnEntity(returnEntity));
        returnEntity.setItems(itemEntities);

        return returnEntity;
    }
}
