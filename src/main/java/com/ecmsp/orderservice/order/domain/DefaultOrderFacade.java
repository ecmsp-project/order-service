package com.ecmsp.orderservice.order.domain;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DefaultOrderFacade implements OrderFacade {

    private final OrderRepository orderRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderReturnabilityService orderReturnabilityService;
    private final Clock clock;

    public DefaultOrderFacade(
            OrderRepository orderRepository,
            OrderIdGenerator orderIdGenerator,
            OrderEventPublisher orderEventPublisher,
            OrderReturnabilityService orderReturnabilityService,
            Clock clock
    ) {
        this.orderRepository = orderRepository;
        this.orderIdGenerator = orderIdGenerator;
        this.orderEventPublisher = orderEventPublisher;
        this.orderReturnabilityService = orderReturnabilityService;
        this.clock = clock;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> findOrderById(OrderId orderId) {
        return orderRepository.findById(orderId);
    }


    public Order createOrder(OrderToCreate orderToCreate) {

        Order order = new Order(
                /* orderId */ orderIdGenerator.generate(null), //generate random UUID
                        orderToCreate.reservationId(), //TODO: should be removed
                /* clientId */ orderToCreate.clientId(),
                /* orderStatus */ OrderStatus.PENDING, // Assuming default status is PENDING
                /* date */ LocalDateTime.now(clock),
                /* items */ orderToCreate.items()
        );

        /*
        TODO: should be sent grpc request to product service to make reservation and whether product variants are accessible
        */

        orderRepository.create(order);


        // event consumed by payment service
        OrderEvent.OrderCreated orderCreatedEvent = new OrderEvent.OrderCreated(
                order.orderId(),
                order.clientId(),
                order.totalPrice(),
                LocalDateTime.now(clock)
        );


        orderEventPublisher.publish(orderCreatedEvent);

        return order;
    }

    public Order updateOrder(OrderToUpdate orderToUpdate) {
        Order currentOrder = orderRepository.findById(orderToUpdate.orderId())
                .orElseThrow(() -> new OrderException.NotFound(orderToUpdate.orderId()));

        Order updatedOrder = new Order(
                currentOrder.orderId(),
                currentOrder.reservationId(),
                currentOrder.clientId(),
                orderToUpdate.newStatus(),
                currentOrder.date(),
                currentOrder.items()
        );


        orderRepository.update(updatedOrder);

        // event consumed by product service
        orderEventPublisher.publish(
                new OrderEvent.OrderStatusUpdated(
                        updatedOrder.orderId(),
                        updatedOrder.orderStatus()
                )
        );

        return updatedOrder;
    }

    public void deleteOrder(OrderId orderId) {
        orderRepository.deleteById(orderId);
    }

    public boolean canOrderBeReturned(OrderId orderId) {
        return orderReturnabilityService.canOrderBeReturned(orderId);
    }

    public List<OrderItem> getReturnableItems(OrderId orderId) {
        return orderReturnabilityService.getReturnableItems(orderId);
    }

    public List<Order> getOrdersByClientId(ClientId clientId) {
        return orderRepository.findByClientId(clientId);
    }

}