package com.ecmsp.orderservice.api.rest.internal;

import com.ecmsp.orderservice.api.rest.internal.dto.OrderIdMappingDto;
import com.ecmsp.orderservice.order.adapter.generator.OrderIdMappingService;
import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/orders")
public class InternalOrdersController {

    private final OrderIdMappingService mappingService;

    public InternalOrdersController(OrderIdMappingService mappingService) {
        this.mappingService = mappingService;
    }

    @ConditionalOnProperty(
            name = "order.id-generator.type",
            havingValue = "fixed",
            matchIfMissing = false
    )
    @PostMapping("/order-id-mappings")
    public ResponseEntity<Void> createOrderIdMapping(@RequestBody OrderIdMappingDto mappingDto) {
        mappingService.mapCorrelationToOrder(new CorrelationId(mappingDto.correlationId()), new OrderId(mappingDto.orderId()));

        return ResponseEntity.ok().build();
    }
}
