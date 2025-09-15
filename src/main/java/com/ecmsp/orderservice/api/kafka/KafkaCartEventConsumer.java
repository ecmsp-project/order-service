package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.Context;
import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderFacade;
import com.ecmsp.orderservice.order.domain.OrderToCreate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class KafkaCartEventConsumer{

    private final OrderFacade orderFacade;

    public KafkaCartEventConsumer(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    @KafkaListener(topics = "${kafka.topic.cart-event}")
    public void consume(CartCreatedEvent cartEvent, @Header(value = "X-Correlation-Id", required = false) String correlationId) {
        try {
            //TODO: we should validate is correlationId is valid UUID string

            MDC.put("correlationId", correlationId.toString());
            log.info("Processing cart event - CorrelationID: {}", correlationId);

            OrderToCreate orderToCreate = cartEvent.toOrder(cartEvent);
            Context context = new Context(new CorrelationId(UUID.fromString(correlationId)));
            orderFacade.createOrder(orderToCreate, context);

            MDC.clear();
            log.info("Finished processing cart event - CorrelationID: {}", correlationId);
        } catch(Throwable e) {
            log.error("Error processing cart event - CorrelationID: {}", correlationId, e);
        }

    }
}
