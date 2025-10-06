package com.ecmsp.orderservice.order.adapter.repository.db.returns;

import com.ecmsp.orderservice.order.domain.returns.ReturnStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Table(name = "returns")
@AllArgsConstructor
@NoArgsConstructor
class ReturnEntity {

    @Id
    @Column(name = "return_id")
    private UUID returnId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReturnStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "returnEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnItemEntity> items;

    public UUID getReturnId() {
        return returnId;
    }

    public void setReturnId(UUID returnId) {
        this.returnId = returnId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ReturnItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ReturnItemEntity> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ReturnEntity{" +
                "returnId=" + returnId +
                ", orderId=" + orderId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
