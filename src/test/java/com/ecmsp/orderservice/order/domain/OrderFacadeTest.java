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
    private static final ReservationId RESERVATION_1_ID = new ReservationId(UUID.fromString("c5e75ab0-a110-4a2a-b6f4-c4573e6f548e"));


    private static final ClientId CLIENT_1_ID = new ClientId(UUID.fromString("b5d1eec8-c3ea-4b55-8cec-900b5c018381"));
    private static final ClientId CLIENT_2_ID = new ClientId(UUID.fromString("b259c7f1-483b-4700-accc-1554542eb8f5"));
    private static final List<OrderItem> ITEMS = List.of(
            new OrderItem(
                    /* itemId = */ new ItemId(UUID.fromString("66d155e8-2d57-44fa-9adc-580e1e4f9cc9")),
                    /* variantId = */ null,
                    /* name = */ "Test Item 1",
                    /* quantity = */ 2,
                    /* price = */ BigDecimal.valueOf(10),
                    /* imageUrl = */ null,
                    /* description = */ null,
                    /* isReturnable = */ true
            ),
            new OrderItem(
                    /* itemId = */ new ItemId(UUID.fromString("473c1579-12b1-49b0-b90e-253782c874a5")),
                    /* variantId = */ null,
                    /* name = */ "Test Item 2",
                    /* quantity = */ 1,
                    /* price = */ BigDecimal.valueOf(20),
                    /* imageUrl = */ null,
                    /* description = */ null,
                    /* isReturnable = */ false
            )
    );


    private static final LocalDateTime DATE_2025_07_10_15_00_00 = LocalDateTime.of(2025, 7, 10, 15, 0, 0);
    private static final LocalDateTime DATE_2025_07_11_15_00_00 = LocalDateTime.of(2025, 7, 11, 6, 0, 0);

    private static final Order ORDER_1 = new Order(
            /* orderId = */ ORDER_1_ID,
            /* reservationId = */ null,
            /* clientId = */ CLIENT_1_ID,
            /* orderStatus = */ OrderStatus.PENDING,
            /* date = */ DATE_2025_07_10_15_00_00,
            /* items = */ Collections.emptyList()
    );

    private static final Order ORDER_2 = new Order(
            /* orderId = */ ORDER_2_ID,
            /* reservationId = */ null,
            /* clientId = */ CLIENT_2_ID,
            /* orderStatus = */ OrderStatus.PENDING,
            /* date = */ DATE_2025_07_11_15_00_00,
            /* items = */ Collections.emptyList()
    );

    @Test
    void should_create_order() {
        // given:
        TestOrderRepository orderRepository = new TestOrderRepository(/* orders = */ Collections.emptyList());
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:

        Order createdOrder = facade.createOrder(new OrderToCreate(RESERVATION_1_ID, CLIENT_1_ID, ITEMS));

        // then:
        assertThat(createdOrder).isEqualTo(
                new Order(
                        /* orderId = */ ORDER_1_ID,
                        /* reservationId = */ RESERVATION_1_ID,
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

        TestOrderRepository orderRepository = new TestOrderRepository(List.of(ORDER_1));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        var error = assertThatThrownBy(() ->
                // Trying to create an order with the same ID: ORDER_1_ID
                facade.createOrder(new OrderToCreate(RESERVATION_1_ID, CLIENT_1_ID, ITEMS))
        );

        // then:
        error.isInstanceOf(OrderException.AlreadyExists.class);
        error.hasMessageContaining("Order with id `%s` already exists".formatted(ORDER_1_ID.value()));
    }

    @Test
    void should_update_order() {

        // given:
        TestOrderRepository orderRepository = new TestOrderRepository(List.of(ORDER_1));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
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
                        /* reservationId = */ null,
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
        TestOrderRepository orderRepository = new TestOrderRepository(Collections.emptyList()); // No existing orders
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
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
        TestOrderRepository orderRepository = new TestOrderRepository(List.of(ORDER_1, ORDER_2));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
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
        TestOrderRepository orderRepository = new TestOrderRepository(List.of(ORDER_1, ORDER_2));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
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
        TestOrderRepository orderRepository = new TestOrderRepository(Collections.emptyList()); // No existing orders
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
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
        TestOrderRepository orderRepository = new TestOrderRepository(List.of(ORDER_1));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
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
        TestOrderRepository orderRepository = new TestOrderRepository(Collections.emptyList()); // No existing orders
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.fixed(DATE_2025_07_10_15_00_00.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        );

        // when:
        facade.deleteOrder(ORDER_1_ID);

        // then:
        assertThat(facade.findOrderById(ORDER_1_ID)).isNotPresent();
    }

    @Test
    void should_return_true_when_order_is_returnable() {
        // given:
        LocalDateTime recentDate = LocalDateTime.now().minusDays(7); // 7 days ago
        List<OrderItem> returnableItems = List.of(
                new OrderItem(
                        new ItemId(UUID.fromString("66d155e8-2d57-44fa-9adc-580e1e4f9cc9")),
                        null,
                        "Test Item",
                        2,
                        BigDecimal.valueOf(10),
                        null,
                        null,
                        true
                )
        );
        Order returnableOrder = new Order(ORDER_1_ID, null, CLIENT_1_ID, OrderStatus.PAID, recentDate, returnableItems);

        TestOrderRepository orderRepository = new TestOrderRepository(List.of(returnableOrder));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.systemDefaultZone()
        );

        // when:
        boolean canBeReturned = facade.canOrderBeReturned(ORDER_1_ID);

        // then:
        assertThat(canBeReturned).isTrue();
    }

    @Test
    void should_return_false_when_order_is_too_old() {
        // given:
        LocalDateTime oldDate = LocalDateTime.now().minusDays(20); // 20 days ago (beyond 14-day limit)
        List<OrderItem> returnableItems = List.of(
                new OrderItem(
                        new ItemId(UUID.fromString("66d155e8-2d57-44fa-9adc-580e1e4f9cc9")),
                        null,
                        "Test Item",
                        2,
                        BigDecimal.valueOf(10),
                        null,
                        null,
                        true
                )
        );
        Order oldOrder = new Order(ORDER_1_ID, null, CLIENT_1_ID, OrderStatus.PAID, oldDate, returnableItems);

        TestOrderRepository orderRepository = new TestOrderRepository(List.of(oldOrder));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.systemDefaultZone()
        );

        // when:
        boolean canBeReturned = facade.canOrderBeReturned(ORDER_1_ID);

        // then:
        assertThat(canBeReturned).isFalse();
    }

    @Test
    void should_return_false_when_no_items_are_returnable() {
        // given:
        LocalDateTime recentDate = LocalDateTime.now().minusDays(7); // 7 days ago
        List<OrderItem> nonReturnableItems = List.of(
                new OrderItem(
                        new ItemId(UUID.fromString("66d155e8-2d57-44fa-9adc-580e1e4f9cc9")),
                        null,
                        "Test Item",
                        2,
                        BigDecimal.valueOf(10),
                        null,
                        null,
                        false // not returnable
                )
        );
        Order nonReturnableOrder = new Order(ORDER_1_ID, null, CLIENT_1_ID, OrderStatus.PAID, recentDate, nonReturnableItems);

        TestOrderRepository orderRepository = new TestOrderRepository(List.of(nonReturnableOrder));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.systemDefaultZone()
        );

        // when:
        boolean canBeReturned = facade.canOrderBeReturned(ORDER_1_ID);

        // then:
        assertThat(canBeReturned).isFalse();
    }

    @Test
    void should_return_only_returnable_items_within_return_period() {
        // given:
        LocalDateTime recentDate = LocalDateTime.now().minusDays(7); // 7 days ago
        OrderItem returnableItem = new OrderItem(
                new ItemId(UUID.fromString("66d155e8-2d57-44fa-9adc-580e1e4f9cc9")),
                null,
                "Test Item 1",
                2,
                BigDecimal.valueOf(10),
                null,
                null,
                true
        );
        OrderItem nonReturnableItem = new OrderItem(
                new ItemId(UUID.fromString("473c1579-12b1-49b0-b90e-253782c874a5")),
                null,
                "Test Item 2",
                1,
                BigDecimal.valueOf(20),
                null,
                null,
                false
        );
        List<OrderItem> mixedItems = List.of(returnableItem, nonReturnableItem);
        Order mixedOrder = new Order(ORDER_1_ID, null, CLIENT_1_ID, OrderStatus.PAID, recentDate, mixedItems);

        TestOrderRepository orderRepository = new TestOrderRepository(List.of(mixedOrder));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.systemDefaultZone()
        );

        // when:
        List<OrderItem> returnableItems = facade.getReturnableItems(ORDER_1_ID);

        // then:
        assertThat(returnableItems).containsExactly(returnableItem);
    }

    @Test
    void should_return_empty_list_when_order_is_outside_return_period() {
        // given:
        LocalDateTime oldDate = LocalDateTime.now().minusDays(20); // 20 days ago
        OrderItem returnableItem = new OrderItem(
                new ItemId(UUID.fromString("66d155e8-2d57-44fa-9adc-580e1e4f9cc9")),
                null,
                "Test Item",
                2,
                BigDecimal.valueOf(10),
                null,
                null,
                true
        );
        Order oldOrder = new Order(ORDER_1_ID, null, CLIENT_1_ID, OrderStatus.PAID, oldDate, List.of(returnableItem));

        TestOrderRepository orderRepository = new TestOrderRepository(List.of(oldOrder));
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.systemDefaultZone()
        );

        // when:
        List<OrderItem> returnableItems = facade.getReturnableItems(ORDER_1_ID);

        // then:
        assertThat(returnableItems).isEmpty();
    }

    @Test
    void should_return_false_when_order_does_not_exist_for_return_check() {
        // given:
        TestOrderRepository orderRepository = new TestOrderRepository(Collections.emptyList());
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.systemDefaultZone()
        );

        // when:
        boolean canBeReturned = facade.canOrderBeReturned(ORDER_1_ID);

        // then:
        assertThat(canBeReturned).isFalse();
    }

    @Test
    void should_return_empty_list_when_order_does_not_exist_for_returnable_items() {
        // given:
        TestOrderRepository orderRepository = new TestOrderRepository(Collections.emptyList());
        OrderFacade facade = new DefaultOrderFacade(
                orderRepository,
                (correlationId) -> ORDER_1_ID,
                new TestOrderEventPublisher(),
                new OrderReturnabilityService(orderRepository),
                Clock.systemDefaultZone()
        );

        // when:
        List<OrderItem> returnableItems = facade.getReturnableItems(ORDER_1_ID);

        // then:
        assertThat(returnableItems).isEmpty();
    }

}
