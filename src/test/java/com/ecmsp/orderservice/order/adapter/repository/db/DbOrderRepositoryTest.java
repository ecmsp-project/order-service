//package com.ecmsp.orderservice.order.adapter.repository.db;
//
//import com.ecmsp.orderservice.order.domain.*;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
////TODO works if jdbc db strategy set as create, update application.yml and version for tests
//@DataJpaTest
//public class DbOrderRepositoryTest {
//
//    private static final OrderId ORDER_1_ID = new OrderId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
//    private static final OrderId ORDER_2_ID = new OrderId(UUID.fromString("9e349a18-1203-4224-829c-dc15700c68a5"));
//
//    private static final ClientId CLIENT_1_ID = new ClientId(UUID.fromString("b5d1eec8-c3ea-4b55-8cec-900b5c018381"));
//    private static final ClientId CLIENT_2_ID = new ClientId(UUID.fromString("b259c7f1-483b-4700-accc-1554542eb8f5"));
//
//    private static final LocalDateTime DATE_2025_07_10_15_00_00 = LocalDateTime.of(2025, 7, 10, 15, 0, 0);
//    private static final LocalDateTime DATE_2025_07_11_15_00_00 = LocalDateTime.of(2025, 7, 11, 6, 0, 0);
//
//    private static final Order ORDER_1 = new Order(
//        /* orderId = */ ORDER_1_ID,
//        /* clientId = */ CLIENT_1_ID,
//        /* orderStatus = */ OrderStatus.PENDING,
//        /* date = */ DATE_2025_07_10_15_00_00,
//        /* items = */ Collections.emptyList()
//    );
//
//    private static final Order ORDER_2 = new Order(
//        /* orderId = */ ORDER_2_ID,
//        /* clientId = */ CLIENT_2_ID,
//        /* orderStatus = */ OrderStatus.PENDING,
//        /* date = */ DATE_2025_07_11_15_00_00,
//        /* items = */ Collections.emptyList()
//    );
//
//    @Autowired
//    private OrderEntityRepository orderEntityRepository;
//
//    @Autowired
//    private TestEntityManager testEntityManager;
//
//    @Test
//    void should_create_new_order() {
//        // given:
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // when:
//        repository.create(ORDER_1);
//
//        // then:
//        Order createdOrder = repository.findById(ORDER_1_ID).orElseThrow();
//        assertThat(createdOrder).isEqualTo(ORDER_1);
//
//        // and:
//        OrderEntity orderEntity = testEntityManager.find(OrderEntity.class, ORDER_1_ID.value());
//        assertThat(orderEntity).isNotNull();
//        assertThat(orderEntity.getClientId()).isEqualTo(CLIENT_1_ID.value());
//        assertThat(orderEntity.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
//        assertThat(orderEntity.getDate()).isEqualTo(DATE_2025_07_10_15_00_00);
//        assertThat(orderEntity.getItems()).isEmpty();
//    }
//
//    @Test
//    void should_throw_exception_when_create_order_with_existing_id() {
//        // given:
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        testEntityManager.persistAndFlush(
//            new OrderEntity(
//                /* orderId = */ ORDER_1_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_10_15_00_00,
//                /* clientId = */ CLIENT_1_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//
//        // when:
//        var error = assertThatThrownBy(() -> {
//            repository.create(ORDER_1); // trying to create order with the same ID
//        });
//
//        // then:
//        error.isInstanceOf(OrderException.AlreadyExists.class);
//        error.hasMessageContaining("Order with id `%s` already exists".formatted(ORDER_1_ID.value()));
//    }
//
//    @Test
//    void should_find_all_orders() {
//        // given:
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        testEntityManager.persist(
//            new OrderEntity(
//                /* orderId = */ ORDER_1_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_10_15_00_00,
//                /* clientId = */ CLIENT_1_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//        testEntityManager.persist(
//            new OrderEntity(
//                /* orderId = */ ORDER_2_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_11_15_00_00,
//                /* clientId = */ CLIENT_2_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//        testEntityManager.flush();
//
//        // when:
//        List<Order> allOrders = repository.findAll();
//
//        // then:
//        assertThat(allOrders).hasSize(2);
//        assertThat(allOrders).containsExactlyInAnyOrder(ORDER_1, ORDER_2);
//    }
//
//    @Test
//    void should_return_empty_list_when_no_order_exists() {
//        // given
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        // no orders created
//
//        // when
//        List<Order> allOrders = repository.findAll();
//
//        // then
//        assertThat(allOrders).isEmpty();
//    }
//
//    @Test
//    void should_find_order_by_id() {
//        // given
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        testEntityManager.persistAndFlush(
//            new OrderEntity(
//                /* orderId = */ ORDER_1_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_10_15_00_00,
//                /* clientId = */ CLIENT_1_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//
//        // when
//        Optional<Order> order = repository.findById(ORDER_1_ID);
//
//        // then
//        assertThat(order).isPresent();
//        assertThat(order.get()).isEqualTo(ORDER_1);
//    }
//
//    @Test
//    void should_return_empty_optional_when_order_with_given_id_not_exist() {
//        // given
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        testEntityManager.persistAndFlush(
//            new OrderEntity(
//                /* orderId = */ ORDER_1_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_10_15_00_00,
//                /* clientId = */ CLIENT_1_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//
//        // when
//        Optional<Order> order = repository.findById(ORDER_2_ID); // ORDER_2_ID does not exist
//
//        // then
//        assertThat(order).isEmpty();
//    }
//
//    @Test
//    void should_update_order() {
//        // given
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        testEntityManager.persistAndFlush(
//            new OrderEntity(
//                /* orderId = */ ORDER_1_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_10_15_00_00,
//                /* clientId = */ CLIENT_1_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//
//        // and:
//        Order updatedOrder = new Order(
//            ORDER_1_ID,
//            CLIENT_1_ID,
//            OrderStatus.PROCESSING,
//            DATE_2025_07_10_15_00_00,
//            Collections.emptyList()
//        );
//
//        // when:
//        repository.update(updatedOrder);
//
//        // then
//        Order order = repository.findById(ORDER_1_ID).orElseThrow();
//        assertThat(order).isEqualTo(updatedOrder);
//
//        // and:
//        OrderEntity orderEntity = testEntityManager.find(OrderEntity.class, ORDER_1_ID.value());
//        assertThat(orderEntity).isNotNull();
//        assertThat(orderEntity.getClientId()).isEqualTo(CLIENT_1_ID.value());
//        assertThat(orderEntity.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
//        assertThat(orderEntity.getDate()).isEqualTo(DATE_2025_07_10_15_00_00);
//        assertThat(orderEntity.getItems()).isEmpty();
//    }
//
//    @Test
//    void should_throw_exception_when_update_order_that_does_not_exist() {
//        // given
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        testEntityManager.persistAndFlush(
//            new OrderEntity(
//                /* orderId = */ ORDER_1_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_10_15_00_00,
//                /* clientId = */ CLIENT_1_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//
//        // and:
//        Order updatedOrder2 = new Order(
//            ORDER_2_ID,
//            CLIENT_2_ID,
//            OrderStatus.PROCESSING,
//            DATE_2025_07_11_15_00_00,
//            Collections.emptyList()
//        );
//
//        // when:
//        var error = assertThatThrownBy(() -> {
//            repository.update(updatedOrder2);
//        });
//
//        // then:
//        error.isInstanceOf(OrderException.NotFound.class);
//        error.hasMessageContaining("Order with id `%s` not found".formatted(ORDER_2_ID.value()));
//    }
//
//    @Test
//    void should_delete_order_by_id() {
//        // given
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        testEntityManager.persistAndFlush(
//            new OrderEntity(
//                /* orderId = */ ORDER_1_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_10_15_00_00,
//                /* clientId = */ CLIENT_1_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//
//        testEntityManager.persistAndFlush(
//            new OrderEntity(
//                /* orderId = */ ORDER_2_ID.value(),
//                /* orderStatus = */ OrderStatus.PENDING,
//                /* date = */ DATE_2025_07_11_15_00_00,
//                /* clientId = */ CLIENT_2_ID.value(),
//                /* items = */ Collections.emptyList()
//            )
//        );
//
//        // when
//        repository.deleteById(ORDER_1_ID);
//
//        // then
//        assertThat(repository.findAll()).hasSize(1);
//        assertThat(repository.findById(ORDER_1_ID)).isEmpty();
//        assertThat(repository.findById(ORDER_2_ID)).isPresent();
//
//        // and:
//        OrderEntity deletedOrderEntity = testEntityManager.find(OrderEntity.class, ORDER_1_ID.value());
//        assertThat(deletedOrderEntity).isNull(); // ORDER_1 should be deleted
//    }
//
//    @Test
//    void should_do_nothing_when_delete_order_that_does_not_exist() {
//        // given
//        OrderRepository repository = new DbOrderRepository(orderEntityRepository);
//
//        // and:
//        repository.create(ORDER_1);
//
//        // when
//        repository.deleteById(ORDER_2_ID); // ORDER_2_ID does not exist
//
//        // then
//        assertThat(repository.findAll()).hasSize(1);
//        assertThat(repository.findById(ORDER_1_ID)).isPresent();
//        assertThat(repository.findById(ORDER_2_ID)).isEmpty();
//
//        // and:
//        OrderEntity deletedOrderEntity = testEntityManager.find(OrderEntity.class, ORDER_2_ID.value());
//        assertThat(deletedOrderEntity).isNull(); // ORDER_2 should not exist
//    }
//
//}
