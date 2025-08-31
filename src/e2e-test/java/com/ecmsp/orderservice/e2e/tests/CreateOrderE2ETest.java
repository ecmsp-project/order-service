package com.ecmsp.orderservice.e2e.tests;

import com.ecmsp.orderservice.api.kafka.CartCreatedEvent;
import com.ecmsp.orderservice.e2e.utils.KafkaApi;
import com.ecmsp.orderservice.e2e.utils.OrderServiceApi;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ecmsp.orderservice.api.kafka.CartCreatedEvent.CartItem;
import static com.ecmsp.orderservice.e2e.E2ETestEnvironment.Containers;
import static com.ecmsp.orderservice.e2e.E2ETestEnvironment.getUrl;

public class CreateOrderE2ETest {

    private static final String CLIENT_ID = "b5d1eec8-c3ea-4b55-8cec-900b5c018381";
    private static final String ITEM_1_ID = "a1d1eec8-c3ea-4b55-8cec-900b5c018381";
    private static final String ITEM_2_ID = "c3d1eec8-c3ea-4b55-8cec-900b5c018381";

    private static final List<CartItem> ITEMS = List.of(
            new CartItem(
                ITEM_1_ID,
                "Item 1",
                new java.math.BigDecimal("10.00"),
                2,
                "Description for Item 1"
            ),
            new CartItem(
                ITEM_2_ID,
                "Item 2",
                new java.math.BigDecimal("20.00"),
                1,
                "Description for Item 2"
            )
    );

    private final KafkaApi kafkaApi;
    private final OrderServiceApi orderServiceApi;


    public CreateOrderE2ETest(){
        this.kafkaApi = new KafkaApi(getUrl(Containers.KAFKA));
        this.orderServiceApi = new OrderServiceApi(getUrl(Containers.ORDER_SERVICE));
    }

    @Test
    public void should_create_order_when_cart_event_sent() {
        CartCreatedEvent event = new CartCreatedEvent(CLIENT_ID, ITEMS);
        kafkaApi.sendEvent(event);

        orderServiceApi.getOrderById();
    }

}
