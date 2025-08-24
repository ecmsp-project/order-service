package com.ecmsp.orderservice.api.rest;

import com.ecmsp.orderservice.api.rest.dto.CreateOrderRequest;
import com.ecmsp.orderservice.api.rest.dto.OrderDetailsResponse;
import com.ecmsp.orderservice.api.rest.dto.UpdateOrderRequest;
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

    @GetMapping
    public ResponseEntity<List<OrderDetailsResponse>> listOrders() {
        List<Order> orders = orderFacade.getAllOrders();
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
    public ResponseEntity<OrderDetailsResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderToCreate orderToCreate = new OrderToCreate(
            /* clientId = */ new ClientId(request.clientId()),
            /* items = */ request.items().stream()
                .map(CreateOrderRequest.Item::toOrderItem)
                .toList()
        );
        Order order = orderFacade.createOrder(orderToCreate);
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

    // TODO: Replace with query params in GET /api/orders
//    @GetMapping("/client/{clientId}")
//    public ResponseEntity<List<OrderEntity>> getOrdersByClientId(@PathVariable UUID clientId) {
//        List<OrderEntity> orders = orderService.getOrdersByClientId(clientId);
//        return ResponseEntity.ok(orders);
//    }

    // TODO: Replace with query params in GET /api/orders
//    @GetMapping("/today")
//    public ResponseEntity<List<OrderEntity>> getTodayOrders() {
//        List<OrderEntity> orders = orderService.getTodayOrders();
//        return ResponseEntity.ok(orders);
//    }


    // TODO: Replace with query params in GET /api/orders
//    @GetMapping("/count/{status}")
//    public ResponseEntity<Long> countOrdersByStatus(@PathVariable OrderStatus status) {
//        long count = orderService.countOrdersByStatus(status);
//        return ResponseEntity.ok(count);
//    }

}