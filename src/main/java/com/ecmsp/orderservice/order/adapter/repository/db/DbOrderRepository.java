package com.ecmsp.orderservice.order.adapter.repository.db;

import com.ecmsp.orderservice.order.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

class DbOrderRepository implements OrderRepository {

    private final OrderEntityRepository orderEntityRepository;
    private final OrderEntityMapper orderEntityMapper = new OrderEntityMapper();

    public DbOrderRepository(OrderEntityRepository orderEntityRepository) {
        this.orderEntityRepository = orderEntityRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderEntityRepository.findAll()
            .stream()
            .map(orderEntityMapper::toOrder)
            .toList();
    }

    @Override
    @Transactional
    public Optional<Order> findById(OrderId orderId) {
        return orderEntityRepository.findById(orderId.value())
            .map(orderEntityMapper::toOrder);
    }

    @Override
    public void create(Order order) {
        if (orderEntityRepository.existsById(order.orderId().value())) {
            throw new OrderException.AlreadyExists(order.orderId());
        }
        orderEntityRepository.save(orderEntityMapper.toOrderEntity(order));
    }

    @Override
    public void update(Order order) {
        if(!orderEntityRepository.existsById(order.orderId().value())) {
            throw new OrderException.NotFound(order.orderId());
        }
        orderEntityRepository.save(orderEntityMapper.toOrderEntity(order));
    }

    @Override
    public void deleteById(OrderId orderId) {
        orderEntityRepository.deleteById(orderId.value());
    }

    @Override
    public List<Order> findByClientId(ClientId clientId) {
        return orderEntityRepository.findByClientId(clientId.value())
            .stream()
            .map(orderEntityMapper::toOrder)
            .toList();
    }

}
