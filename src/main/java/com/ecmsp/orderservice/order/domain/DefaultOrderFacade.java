package com.ecmsp.orderservice.order.domain;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class DefaultOrderFacade implements OrderFacade {

    private final OrderRepository orderRepository;
    private final Supplier<OrderId> orderIdGenerator;
    private final PaymentClient paymentClient;
    private final OrderEventPublisher orderEventPublisher;
    private final Clock clock;


    public DefaultOrderFacade(
            OrderRepository orderRepository,
            Supplier<OrderId> orderIdGenerator,
            PaymentClient paymentEventPublisher,
            OrderEventPublisher orderEventPublisher,
            Clock clock
    ) {
        this.orderRepository = orderRepository;
        this.orderIdGenerator = orderIdGenerator;
        this.paymentClient = paymentEventPublisher;
        this.orderEventPublisher = orderEventPublisher;
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
                /* orderId */ orderIdGenerator.get(),
                /* clientId */ orderToCreate.clientId(),
                /* orderStatus */ OrderStatus.PENDING, // Assuming default status is PENDING
                /* date */ LocalDateTime.now(clock),
                /* items */ orderToCreate.items()
        );

        orderRepository.create(order);

        PaymentToCreate paymentToCreate = new PaymentToCreate(
                order.orderId(),
                order.clientId(),
                order.totalPrice(),
                LocalDateTime.now(clock)
        );

        paymentClient.createPayment(paymentToCreate);

        return order;
    }

    public Order updateOrder(OrderToUpdate orderToUpdate) {
        Order currentOrder = orderRepository.findById(orderToUpdate.orderId())
                .orElseThrow(() -> new OrderException.NotFound(orderToUpdate.orderId()));

        Order updatedOrder = new Order(
                currentOrder.orderId(),
                currentOrder.clientId(),
                orderToUpdate.newStatus(),
                currentOrder.date(),
                currentOrder.items()
        );

        orderRepository.update(updatedOrder);
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
}