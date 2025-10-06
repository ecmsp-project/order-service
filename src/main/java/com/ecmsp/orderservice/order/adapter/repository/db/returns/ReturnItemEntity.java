package com.ecmsp.orderservice.order.adapter.repository.db.returns;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Entity
@Table(name = "return_item")
@NoArgsConstructor
@AllArgsConstructor
class ReturnItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID returnItemId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "variant_id", nullable = false)
    private UUID variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false)
    private ReturnEntity returnEntity;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reason")
    private String reason;

    public UUID getReturnItemId() {
        return returnItemId;
    }

    public void setReturnItemId(UUID returnItemId) {
        this.returnItemId = returnItemId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public void setVariantId(UUID variantId) {
        this.variantId = variantId;
    }

    public ReturnEntity getReturnEntity() {
        return returnEntity;
    }

    public void setReturnEntity(ReturnEntity returnEntity) {
        this.returnEntity = returnEntity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
