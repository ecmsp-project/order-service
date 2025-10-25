package com.ecmsp.orderservice.api.kafka;

import com.ecmsp.orderservice.order.domain.*;
import com.ecmsp.orderservice.order.adapter.publisher.kafka.KafkaOrderCreatedEvent;
import com.ecmsp.orderservice.order.adapter.publisher.kafka.KafkaOrderStatusUpdatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {
                "payment-processed-succeeded",
                "payment-processed-failed",
                "order-created",
                "order-status-updated"
        },
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@Import(KafkaPaymentEventIntegrationTest.TestKafkaEventListener.class)
class KafkaPaymentEventIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private TestKafkaEventListener testEventListener;

    @BeforeEach
    void setUp() {
        testEventListener.clearRecords();

        // Wait for all Kafka listeners to be assigned
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, 1);
        }
    }

    @AfterEach
    void tearDown() {
        testEventListener.clearRecords();
    }

    @Component
    public static class TestKafkaEventListener {
        private final BlockingQueue<KafkaOrderCreatedEvent> orderCreatedRecords = new LinkedBlockingQueue<>();
        private final BlockingQueue<KafkaOrderStatusUpdatedEvent> orderStatusUpdatedRecords = new LinkedBlockingQueue<>();

        @Autowired
        private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

        @KafkaListener(topics = "order-created", groupId = "test-order-created-consumer")
        public void consumeOrderCreated(@Payload String eventJson) {
            try {
                KafkaOrderCreatedEvent event = objectMapper.readValue(eventJson, KafkaOrderCreatedEvent.class);
                orderCreatedRecords.add(event);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize KafkaOrderCreatedEvent", e);
            }
        }

        @KafkaListener(topics = "order-status-updated", groupId = "test-order-status-updated-consumer")
        public void consumeOrderStatusUpdated(@Payload String eventJson) {
            try {
                KafkaOrderStatusUpdatedEvent event = objectMapper.readValue(eventJson, KafkaOrderStatusUpdatedEvent.class);
                orderStatusUpdatedRecords.add(event);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize KafkaOrderStatusUpdatedEvent", e);
            }
        }

        public KafkaOrderCreatedEvent pollOrderCreated(long timeout, TimeUnit unit) throws InterruptedException {
            return orderCreatedRecords.poll(timeout, unit);
        }

        public KafkaOrderStatusUpdatedEvent pollOrderStatusUpdated(long timeout, TimeUnit unit) throws InterruptedException {
            return orderStatusUpdatedRecords.poll(timeout, unit);
        }

        public void clearRecords() {
            orderCreatedRecords.clear();
            orderStatusUpdatedRecords.clear();
        }
    }

    @Test
    void should_update_order_to_paid_and_publish_status_updated_event_when_payment_succeeded_event_consumed() throws Exception {
        // Given: Create an order with PENDING status
        ClientId clientId = new ClientId(UUID.randomUUID());

        OrderToCreate orderToCreate = new OrderToCreate(
                null,
                clientId,
                List.of(
                        new OrderItem(
                                new ItemId(UUID.randomUUID()),
                                new VariantId(UUID.randomUUID()),
                                2,
                                new BigDecimal("100.00"),
                                null,
                                "Test Description",
                                false
                        )
                )
        );

        Context context = new Context(new CorrelationId(UUID.randomUUID()));
        Order createdOrder = orderFacade.createOrder(orderToCreate, context);

        assertThat(createdOrder.orderStatus()).isEqualTo(OrderStatus.PENDING);

        // Clear the OrderCreated event from the queue
        testEventListener.clearRecords();

        // When: Send PaymentProcessedSucceededEvent
        KafkaPaymentProcessedSucceededEvent paymentEvent = new KafkaPaymentProcessedSucceededEvent(
                createdOrder.orderId().value().toString(),
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString()
        );

        String paymentEventJson = objectMapper.writeValueAsString(paymentEvent);
        kafkaTemplate.send("payment-processed-succeeded", paymentEvent.orderId(), paymentEventJson);
        kafkaTemplate.flush();

        // Then: Verify order status is updated to PAID
        Thread.sleep(2000); // Wait for async processing

        Order updatedOrder = orderFacade.findOrderById(createdOrder.orderId())
                .orElseThrow(() -> new AssertionError("Order not found"));

        assertThat(updatedOrder.orderStatus()).isEqualTo(OrderStatus.PAID);

        // And: Verify OrderStatusUpdatedEvent is published to Kafka
        KafkaOrderStatusUpdatedEvent statusEvent = testEventListener.pollOrderStatusUpdated(10, TimeUnit.SECONDS);
        assertThat(statusEvent).isNotNull();
        assertThat(statusEvent.orderId()).isEqualTo(createdOrder.orderId().value().toString());
        assertThat(statusEvent.status()).isEqualTo("PAID");
    }

    @Test
    void should_update_order_to_failed_and_publish_status_updated_event_when_payment_failed_event_consumed() throws Exception {
        // Given: Create an order with PENDING status
        ClientId clientId = new ClientId(UUID.randomUUID());

        OrderToCreate orderToCreate = new OrderToCreate(
                null,
                clientId,
                List.of(
                        new OrderItem(
                                new ItemId(UUID.randomUUID()),
                                new VariantId(UUID.randomUUID()),
                                1,
                                new BigDecimal("100.00"),
                                null,
                                "Test Description",
                                false
                        )
                )
        );

        Context context = new Context(new CorrelationId(UUID.randomUUID()));
        Order createdOrder = orderFacade.createOrder(orderToCreate, context);

        assertThat(createdOrder.orderStatus()).isEqualTo(OrderStatus.PENDING);

        // Clear the OrderCreated event from the queue
        testEventListener.clearRecords();

        // When: Send PaymentProcessedFailedEvent
        KafkaPaymentProcessedFailedEvent paymentEvent = new KafkaPaymentProcessedFailedEvent(
                createdOrder.orderId().value().toString(),
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString()
        );

        String paymentEventJson = objectMapper.writeValueAsString(paymentEvent);
        kafkaTemplate.send("payment-processed-failed", paymentEvent.orderId(), paymentEventJson);
        kafkaTemplate.flush();

        // Then: Verify order status is updated to FAILED
        Thread.sleep(2000); // Wait for async processing

        Order updatedOrder = orderFacade.findOrderById(createdOrder.orderId())
                .orElseThrow(() -> new AssertionError("Order not found"));

        assertThat(updatedOrder.orderStatus()).isEqualTo(OrderStatus.FAILED);

        // And: Verify OrderStatusUpdatedEvent is published to Kafka
        KafkaOrderStatusUpdatedEvent statusEvent = testEventListener.pollOrderStatusUpdated(10, TimeUnit.SECONDS);
        assertThat(statusEvent).isNotNull();
        assertThat(statusEvent.orderId()).isEqualTo(createdOrder.orderId().value().toString());
        assertThat(statusEvent.status()).isEqualTo("FAILED");
    }

    @Test
    void should_publish_order_created_event_to_kafka_when_order_is_created() throws Exception {
        // Given: Order details
        ClientId clientId = new ClientId(UUID.randomUUID());
        BigDecimal itemPrice = new BigDecimal("50.00");
        int quantity = 3;
        // Note: totalPrice() in Order just sums prices without multiplying by quantity

        OrderToCreate orderToCreate = new OrderToCreate(
                null,
                clientId,
                List.of(
                        new OrderItem(
                                new ItemId(UUID.randomUUID()),
                                new VariantId(UUID.randomUUID()),
                                quantity,
                                itemPrice,
                                null,
                                "Product Description",
                                false
                        )
                )
        );

        Context context = new Context(new CorrelationId(UUID.randomUUID()));

        // When: Create order
        Order createdOrder = orderFacade.createOrder(orderToCreate, context);

        // Then: Verify OrderCreatedEvent is published to Kafka
        KafkaOrderCreatedEvent event = testEventListener.pollOrderCreated(10, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.orderId()).isEqualTo(createdOrder.orderId().value().toString());
        assertThat(event.clientId()).isEqualTo(clientId.value().toString());
        assertThat(event.orderTotal()).isEqualByComparingTo(itemPrice);
        assertThat(event.requestedAt()).isNotNull();
    }
}
