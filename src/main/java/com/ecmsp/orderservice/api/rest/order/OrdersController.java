package com.ecmsp.orderservice.api.rest.order;

import com.ecmsp.orderservice.api.rest.order.dto.CreateOrderRequest;
import com.ecmsp.orderservice.api.rest.order.dto.OrderDetailsResponse;
import com.ecmsp.orderservice.api.rest.order.dto.OrderReturnabilityResponse;
import com.ecmsp.orderservice.api.rest.order.dto.UpdateOrderRequest;
import com.ecmsp.orderservice.application.security.UserContext;
import com.ecmsp.orderservice.application.security.UserContextData;
import com.ecmsp.orderservice.order.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrderFacade orderFacade;
    private final OrderControllerMapper orderMapper;

    @Autowired
    public OrdersController(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
        this.orderMapper = new OrderControllerMapper();
    }


    //TODO: authorization for this endpoint (only admin can see all orders) - use userContext and retrieve group / role from there
    @GetMapping
    public ResponseEntity<List<OrderDetailsResponse>> listOrders() {
        List<Order> orders = orderFacade.getAllOrders();
        return ResponseEntity.ok(orders.stream().map(orderMapper::toOrderDetailsResponse).toList());
    }


    //! use for tests - gateway calls
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDetailsResponse>> listOrdersByUserId(@UserContext UserContextData userContextData) {
        ClientId clientId = new ClientId(UUID.fromString(userContextData.userId()));
        List<Order> orders = orderFacade.getOrdersByClientId(clientId);
        return ResponseEntity.ok(orders.stream().map(orderMapper::toOrderDetailsResponse).toList());
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderById(@PathVariable UUID orderId) {
        Optional<Order> order = orderFacade.findOrderById(new OrderId(orderId));

        return order.map(orderMapper::toOrderDetailsResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    public ResponseEntity<OrderDetailsResponse> createOrder(@RequestBody CreateOrderRequest request, @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {
        Context context = new Context(new CorrelationId(UUID.fromString(correlationId)));
        OrderToCreate orderToCreate = new OrderToCreate(
            /* clientId = */ new ClientId(request.clientId()),
            /* items = */ request.items().stream()
                .map(CreateOrderRequest.Item::toOrderItem)
                .toList()
        );

        Order order = orderFacade.createOrder(orderToCreate, context);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toOrderDetailsResponse(order));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> updateOrder(
        @PathVariable("orderId") UUID orderId,
        @RequestBody UpdateOrderRequest request
    ) {
        Order updatedOrder = orderFacade.updateOrder(
            new OrderToUpdate(
                /* orderId = */ new OrderId(orderId),
                /* newStatus = */ request.orderStatus()
            )
        );

        return ResponseEntity.ok(orderMapper.toOrderDetailsResponse(updatedOrder));
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") UUID orderId) {
        orderFacade.deleteOrder(new OrderId(orderId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/returnability")
    public ResponseEntity<OrderReturnabilityResponse> getOrderReturnability(@PathVariable UUID orderId) {
        Optional<Order> order = orderFacade.findOrderById(new OrderId(orderId));

        return order.map(orderMapper::toOrderReturnabilityResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{orderId}/returnable")
    public ResponseEntity<Boolean> canOrderBeReturned(@PathVariable UUID orderId) {
        boolean canBeReturned = orderFacade.canOrderBeReturned(new OrderId(orderId));
        return ResponseEntity.ok(canBeReturned);
    }



}