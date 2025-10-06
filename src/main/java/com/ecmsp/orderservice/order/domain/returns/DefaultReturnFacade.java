package com.ecmsp.orderservice.order.domain.returns;

import com.ecmsp.orderservice.order.domain.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DefaultReturnFacade implements ReturnFacade {

    private final ReturnRepository returnRepository;
    private final OrderRepository orderRepository;
    private final OrderReturnabilityService orderReturnabilityService;
    private final ReturnIdGenerator returnIdGenerator;
    private final Clock clock;

    public DefaultReturnFacade(ReturnRepository returnRepository, OrderRepository orderRepository, OrderReturnabilityService orderReturnabilityService, ReturnIdGenerator returnIdGenerator, Clock clock) {
        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
        this.orderReturnabilityService = orderReturnabilityService;
        this.returnIdGenerator = returnIdGenerator;
        this.clock = clock;
    }

    @Override
    public ReturnOrder createReturn(ReturnToCreate returnToCreate) throws OrderException.OrderNotReturnable {
        Order order = orderRepository.findById(returnToCreate.orderId())
                .orElseThrow(() -> new OrderException.NotFound(returnToCreate.orderId()));

        if (!orderReturnabilityService.canOrderBeReturned(order.orderId())) {
            throw new OrderException.OrderNotReturnable(order.orderId());
        }

        //TODO: context should be add here as in order facade
        ReturnId returnId = returnIdGenerator.generate(null);

        List<ItemToReturnDetails> itemsThatCanBeReturned = retrieveItemsThatCanBeReturned(returnToCreate, order);
        ReturnOrder newReturn = new ReturnOrder(
                returnId,
                returnToCreate.orderId(),
                itemsThatCanBeReturned,
                ReturnStatus.REQUESTED,
                LocalDateTime.now(clock)
        );

        return returnRepository.save(newReturn);
    }

    private List<ItemToReturnDetails> retrieveItemsThatCanBeReturned(ReturnToCreate returnToCreate, Order order) {
        List<OrderItem> returnableItems = orderReturnabilityService.getReturnableItems(order.orderId());
        return returnToCreate.itemsToReturn().stream()
                .filter(item -> returnableItems.stream()
                        .anyMatch(returnableItem -> returnableItem.itemId().equals(item.itemId()))
                ).toList();
    }

    @Override
    public Optional<ReturnOrder> findReturnById(ReturnId returnId) {
        return returnRepository.findById(returnId);
    }

    @Override
    public List<ReturnOrder> getAllReturns() {
        return returnRepository.findAll();
    }

    @Override
    public List<ReturnOrder> getReturnsByUserId(ClientId clientId) {
        List<Order> userOrders = orderRepository.findByClientId(clientId);

        return userOrders.stream()
                .flatMap(order -> returnRepository.findByOrderId(order.orderId()).stream())
                .toList();
    }
}
