package com.ecmsp.orderservice.order.adapter.repository.db.returns;

import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnOrder;
import com.ecmsp.orderservice.order.domain.returns.ReturnRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class DbReturnRepository implements ReturnRepository {

    private final ReturnEntityRepository returnEntityRepository;
    private final ReturnEntityMapper returnEntityMapper = new ReturnEntityMapper();

    public DbReturnRepository(ReturnEntityRepository returnEntityRepository) {
        this.returnEntityRepository = returnEntityRepository;
    }

    @Override
    @Transactional
    public ReturnOrder save(ReturnOrder returnOrderDomain) {
        ReturnEntity savedEntity = returnEntityRepository.save(
            returnEntityMapper.toReturnEntity(returnOrderDomain)
        );
        return returnEntityMapper.toReturnOrder(savedEntity);
    }

    @Override
    @Transactional
    public Optional<ReturnOrder> findById(ReturnId returnId) {
        return returnEntityRepository.findById(returnId.id())
            .map(returnEntityMapper::toReturnOrder);
    }

    @Override
    public List<ReturnOrder> findAll() {
        return returnEntityRepository.findAll()
            .stream()
            .map(returnEntityMapper::toReturnOrder)
            .toList();
    }

    @Override
    @Transactional
    public List<ReturnOrder> findByOrderId(OrderId orderId) {
        return returnEntityRepository.findByOrderId(orderId.value())
            .stream()
            .map(returnEntityMapper::toReturnOrder)
            .toList();
    }
}
