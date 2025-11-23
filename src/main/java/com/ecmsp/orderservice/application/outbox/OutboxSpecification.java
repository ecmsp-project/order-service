package com.ecmsp.orderservice.application.outbox;

public interface OutboxSpecification<E> {

    String getPayload(E event);
    String getEventType(E event);

    void onProcess(String payload, String eventType);

}
