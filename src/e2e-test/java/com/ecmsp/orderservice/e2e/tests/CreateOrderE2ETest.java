package com.ecmsp.orderservice.e2e.tests;

import com.ecmsp.orderservice.e2e.utils.InternalOrderApi;
import com.ecmsp.orderservice.e2e.utils.KafkaApi;
import com.ecmsp.orderservice.e2e.utils.OrderServiceApi;

import java.util.List;

import static com.ecmsp.orderservice.api.kafka.KafkaCartCreatedEvent.CartItem;
import static com.ecmsp.orderservice.e2e.E2ETestEnvironment.*;


public class CreateOrderE2ETest {

    private static final String CLIENT_ID = "b5d1eec8-c3ea-4b55-8cec-900b5c018381";
    private static final String ITEM_1_ID = "a1d1eec8-c3ea-4b55-8cec-900b5c018381";
    private static final String ITEM_2_ID = "c3d1eec8-c3ea-4b55-8cec-900b5c018381";
    private static final String ORDER_ID = "d4d1eec8-c3ea-4b55-8cec-900b5c018381";
    private static final String CORRELATION_ID = "e5d1eec8-c3ea-4b55-8cec-900b5c018381";

    private static final List<CartItem> ITEMS = List.of(
            new CartItem(
                    ITEM_1_ID,
                    null,
                    "Item 1",
                    new java.math.BigDecimal("10.00"),
                    2,
                    null,
                    "Description for Item 1",
                    false
            ),
            new CartItem(
                    ITEM_2_ID,
                    null,
                    "Item 2",
                    new java.math.BigDecimal("20.00"),
                    1,
                    null,
                    "Description for Item 2",
                    true
            )
    );

    private final KafkaApi kafkaApi;
    private final OrderServiceApi orderServiceApi;
    private final InternalOrderApi internalOrderApi;


    public CreateOrderE2ETest() {
        this.internalOrderApi = new InternalOrderApi(orderServiceRestUrl());
        this.kafkaApi = new KafkaApi(kafkaBootstrapServers());
        this.orderServiceApi = new OrderServiceApi(orderServiceGrpcUrl());
    }

//    @Test
//    public void should_create_order_when_cart_event_sent() throws InterruptedException {
//
//        internalOrderApi.createOrderMapping(CORRELATION_ID, ORDER_ID);
//        CartCreatedEvent event = new CartCreatedEvent(CLIENT_ID, ITEMS);
//        kafkaApi.sendEvent(event, CORRELATION_ID);
//        Thread.sleep(10000); // TODO: use retryable mechanism here
//        GetOrderResponse orderResponse = orderServiceApi.getOrderById(ORDER_ID);
//
//        assertThat(orderResponse.getItemsList()).containsExactly(
//                OrderItemDetails.newBuilder()
//                        .setItemId(ITEM_1_ID)
//                        .setQuantity(2)
//                        .build(),
//                OrderItemDetails.newBuilder()
//                        .setItemId(ITEM_2_ID)
//                        .setQuantity(1)
//                        .build()
//                );
//    }

}
