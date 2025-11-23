package com.ecmsp.orderservice.application.outbox;

public class Outbox<E> {

    private final OutboxSpecification<E> specification;

    public Outbox(OutboxSpecification<E> specification) {
        this.specification = specification;
    }

    public void publish(E event) {
        try {
            kafkaEventRepository.save(
                KafkaEventEntity.builder()
                    .payload(specification.getPayload(event))
                    .eventType(specification.getEventType(event))
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to publish event to outbox.", e);
        }

    }

}
