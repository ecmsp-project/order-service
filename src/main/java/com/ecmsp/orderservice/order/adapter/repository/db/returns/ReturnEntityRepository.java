package com.ecmsp.orderservice.order.adapter.repository.db.returns;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReturnEntityRepository extends JpaRepository<ReturnEntity, UUID> {
    List<ReturnEntity> findByOrderId(UUID orderId);
}
