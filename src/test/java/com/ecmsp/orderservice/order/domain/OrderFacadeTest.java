package com.ecmsp.orderservice.order.domain;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderFacadeTest {


    private static final OrderId ORDER_1_ID = new OrderId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    private static final OrderId ORDER_2_ID = new OrderId(UUID.fromString("9e349a18-1203-4224-829c-dc15700c68a5"));

    private static final ClientId CLIENT_1_ID = new ClientId(UUID.fromString("b5d1eec8-c3ea-4b55-8cec-900b5c018381"));
    private static final ClientId CLIENT_2_ID = new ClientId(UUID.fromString("b259c7f1-483b-4700-accc-1554542eb8f5"));
    private static final List<OrderItem> ITEMS = List.of(
            new OrderItem(
                    /* itemId = */ new ItemId(UUID.fromString("66d155e8-2d57-44fa-9adc-580e1e4f9cc9")),
                    /* quantity = */ 2,
                    /* price = */ BigDecimal.valueOf(10)
            ),
            new OrderItem(
                    /* itemId = */ new ItemId(UUID.fromString("473c1579-12b1-49b0-b90e-253782c874a5")),
                    /* quantity = */ 1,
                    /* price = */ BigDecimal.valueOf(20)
            )
    );


    private static final LocalDateTime DATE_2025_07_10_15_00_00 = LocalDateTime.of(2025, 7, 10, 15, 0, 0);
    private static final LocalDateTime DATE_2025_07_11_15_00_00 = LocalDateTime.of(2025, 7, 11, 6, 0, 0);

    private static final Order ORDER_1 = new Order(
            /* orderId = */ ORDER_1_ID,
            /* clientId = */ CLIENT_1_ID,
            /* orderStatus = */ OrderStatus.PENDING,
            /* date = */ DATE_2025_07_10_15_00_00,
            /* items = */ Collections.emptyList()
    );

    private static final Order ORDER_2 = new Order(
            /* orderId = */ ORDER_2_ID,
            /* clientId = */ CLIENT_2_ID,
            /* orderStatus = */ OrderStatus.PENDING,
            /* date = */ DATE_2025_07_11_15_00_00,
            /* items = */ Collections.emptyList()
    );

    @Test
    void should_create_order() {
        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(/* orders = */ Collections.emptyList()),
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:

        Order createdOrder = facade.createOrder(new OrderToCreate(CLIENT_1_ID, ITEMS), new Context(null));

        // then:
        assertThat(createdOrder).isEqualTo(
                new Order(
                        /* orderId = */ ORDER_1_ID,
                        /* clientId = */ CLIENT_1_ID,
                        /* orderStatus = */ OrderStatus.PENDING,
                        /* date = */ DATE_2025_07_10_15_00_00,
                        /* items = */ ITEMS
                )
        );
    }

    @Test
    void should_fail_when_create_order_with_existing_id() {
        // given:

        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(List.of(ORDER_1)),
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        var error = assertThatThrownBy(() ->
                // Trying to create an order with the same ID: ORDER_1_ID
                facade.createOrder(new OrderToCreate(CLIENT_1_ID, ITEMS), new Context(null))
        );

        // then:
        error.isInstanceOf(OrderException.AlreadyExists.class);
        error.hasMessageContaining("Order with id `%s` already exists".formatted(ORDER_1_ID.value()));
    }

    @Test
    void should_update_order() {

        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(List.of(ORDER_1)),
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        Order updatedOrder = facade.updateOrder(
                new OrderToUpdate(
                        /* orderId = */ ORDER_1_ID,
                        /* newStatus = */ OrderStatus.PAID
                )
        );

        // then:
        assertThat(updatedOrder).isEqualTo(
                new Order(
                        /* orderId = */ ORDER_1_ID,
                        /* clientId = */ CLIENT_1_ID,
                        /* orderStatus = */ OrderStatus.PAID,
                        /* date = */ DATE_2025_07_10_15_00_00,
                        /* items = */ Collections.emptyList()
                )
        );
    }

    @Test
    void should_fail_when_update_order_with_non_existing_id() {
        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(Collections.emptyList()), // No existing orders
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        var error = assertThatThrownBy(() ->
                // Trying to update an order with a non-existing ID: ORDER_1_ID
                facade.updateOrder(new OrderToUpdate(ORDER_1_ID, OrderStatus.PAID))
        );

        // then:
        error.isInstanceOf(OrderException.NotFound.class);
        error.hasMessageContaining("Order with id `%s` not found".formatted(ORDER_1_ID.value()));
    }

    @Test
    void should_list_all_orders() {
        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(List.of(ORDER_1, ORDER_2)),
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        List<Order> allOrders = facade.getAllOrders();

        // then:
        assertThat(allOrders).containsExactlyInAnyOrder(ORDER_1, ORDER_2);
    }

    @Test
    void should_find_order_by_id() {
        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(List.of(ORDER_1, ORDER_2)),
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        var foundOrder = facade.findOrderById(ORDER_1_ID);

        // then:
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get()).isEqualTo(ORDER_1);
    }

    @Test
    void should_not_find_order_when_id_does_not_exist() {
        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(Collections.emptyList()), // No existing orders
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        var foundOrder = facade.findOrderById(ORDER_1_ID);

        // then:
        assertThat(foundOrder).isNotPresent();
    }

    @Test
    void should_delete_order() {
        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(List.of(ORDER_1)),
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        facade.deleteOrder(ORDER_1_ID);

        // then:
        assertThat(facade.findOrderById(ORDER_1_ID)).isNotPresent();
    }

    @Test
    void should_do_nothing_when_delete_non_existing_order() {
        // given:
        OrderFacade facade = new DefaultOrderFacade(
                new TestOrderRepository(Collections.emptyList()), // No existing orders
                (correlationId) -> ORDER_1_ID,
                new TestPaymentEventPublisher(),
                new TestOrderEventPublisher(),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        facade.deleteOrder(ORDER_1_ID);

        // then:
        assertThat(facade.findOrderById(ORDER_1_ID)).isNotPresent();
    }

}
