package com.ecmsp.orderservice.order.adapter.publisher.outbox3;

import com.ecmsp.orderservice.application.outbox.Outbox;
import com.ecmsp.orderservice.application.outbox.OutboxSpecification;
import com.ecmsp.orderservice.order.domain.OrderEvent;
import com.ecmsp.orderservice.order.domain.OrderEventPublisher;

public class OutboxedOrderEventPublisher  {

    private final Outbox<OrderEventOutboxSpecification> outbox;
    private final OrderEventPublisher delegate;

    public OutboxedOrderEventPublisher(OrderEventPublisher delegate) {
        this.outbox = new Outbox<>(
            new OutboxSpecification<OrderEvent>() {
                @Override
                public String getPayload(OrderEvent event) {
                    return switch (event) {
                        case OrderEvent.OrderCreated -> "OrderCreated";
                        case OrderEvent.OrderStatusUpdated -> objectMapper,writeValueAsString
                    }
                }

                @Override
                public String getEventType(OrderEventOutboxSpecification event) {
                    return event.getClass().getCanonicalName();
                }

                @Override
                public void onProcess(String payload, String eventType) {
                    OrderEvent orderEvent = fromPayloadAndType(payload, eventType);
                    delegate.publish(orderEvent);
                }
            }
        )
    }
}
