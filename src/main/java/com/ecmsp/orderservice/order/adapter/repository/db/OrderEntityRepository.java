package com.ecmsp.orderservice.order.adapter.repository.db;

import com.ecmsp.orderservice.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
interface OrderEntityRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByOrderStatus(OrderStatus orderStatus);
    List<OrderEntity> findByClientId(UUID clientId);
    List<OrderEntity> findByDateAfter(LocalDateTime date);
    List<OrderEntity> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.orderStatus = :status")
    long countByOrderStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM OrderEntity o WHERE o.clientId = :clientId ORDER BY o.date DESC")
    List<OrderEntity> findRecentOrdersByClient(@Param("clientId") UUID clientId);

    List<OrderEntity> findByDateBefore(LocalDateTime date);
}