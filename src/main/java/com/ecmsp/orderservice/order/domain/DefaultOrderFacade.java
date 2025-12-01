package com.ecmsp.orderservice.order.domain;

import com.ecmsp.orderservice.order.domain.reservation.ReservationClient;
import com.ecmsp.orderservice.order.domain.reservation.ReservationCreated;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DefaultOrderFacade implements OrderFacade {

    private final OrderRepository orderRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderReturnabilityService orderReturnabilityService;
    private final ReservationClient reservationClient;
    private final OrderMapper orderMapper;
    private final Clock clock;

    public DefaultOrderFacade(
            OrderRepository orderRepository,
            OrderIdGenerator orderIdGenerator,
            OrderEventPublisher orderEventPublisher,
            OrderReturnabilityService orderReturnabilityService,
            ReservationClient reservationClient, OrderMapper orderMapper,
            Clock clock
    ) {
        this.orderRepository = orderRepository;
        this.orderIdGenerator = orderIdGenerator;
        this.orderEventPublisher = orderEventPublisher;
        this.orderReturnabilityService = orderReturnabilityService;
        this.reservationClient = reservationClient;
        this.orderMapper = orderMapper;
        this.clock = clock;
    }
    @Transactional
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> findOrderById(OrderId orderId) {
        return orderRepository.findById(orderId);
    }


    public OrderCreated createOrder(OrderToCreate orderToCreate) {

        System.out.println("Creating order for client: " + orderToCreate.clientId());
        OrderId orderId = orderIdGenerator.generate(null); //generate random UUID

        Order order = new Order(
                /* orderId */ orderId,
                /* reservationId */ orderToCreate.reservationId(), //TODO: should be removed
                /* clientId */ orderToCreate.clientId(),
                /* orderStatus */ OrderStatus.PENDING,
                /* date */ LocalDateTime.now(clock),
                /* items */ orderToCreate.items()
        );

        System.out.println("Call reservation service to reserve variants for order: " + orderId);
        ReservationCreated reservationCreated = reservationClient.createReservation(orderMapper.toReservationToCreate(order));
        System.out.println("Reservation service responded for order: " + orderId);

        // Check if any variants failed to be reserved (insufficient stock)
        boolean isReservationSuccessful = reservationCreated.variantsOutOfStock().isEmpty();
        if(isReservationSuccessful){
            orderRepository.create(order);

            // event consumed by payment service
            OrderEvent.OrderCreated orderCreatedEvent = new OrderEvent.OrderCreated(
                    order.orderId(),
                    order.clientId(),
                    order.totalPrice(),
                    LocalDateTime.now(clock)
            );


            orderEventPublisher.publish(orderCreatedEvent);
        }

        return new OrderCreated(
                isReservationSuccessful,
                isReservationSuccessful ? orderId : null,
                reservationCreated
        );


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