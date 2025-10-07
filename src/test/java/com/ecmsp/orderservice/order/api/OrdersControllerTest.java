package com.ecmsp.orderservice.order.api;

import com.ecmsp.orderservice.api.rest.order.OrdersController;
import com.ecmsp.orderservice.order.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ecmsp.orderservice.utils.MockMvcAssertionsUtils.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(OrdersController.class)
@Import(OrdersControllerTestConfiguration.class)
public class OrdersControllerTest {

    private static final Order ORDER_1 = new Order(
        /* orderId = */ new OrderId(UUID.fromString("3745fd3b-62b1-40a1-ab32-57aa2ecf562f")),
        /* reservationId = */ null,
        /* clientId = */ new ClientId(UUID.fromString("b74d2425-a3ad-4138-ae70-3c4ecbcf5803")),
        /* orderStatus = */ OrderStatus.PENDING,
        /* date = */ LocalDateTime.of(2025, 7, 10, 15, 0, 0),
        /* items = */ Collections.emptyList()
    );

    private static final Order ORDER_2 = new Order(
        /* orderId = */ new OrderId(UUID.fromString("605c2114-faaa-447d-adfd-582408389958")),
        /* reservationId = */ null,
        /* clientId = */ new ClientId(UUID.fromString("85f30610-467c-49a1-a50a-9686aa6089dc")),
        /* orderStatus = */ OrderStatus.PENDING,
        /* date = */ LocalDateTime.of(2025, 7, 11, 6, 0, 0),
        /* items = */ Collections.emptyList()
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderFacade orderFacade;

    @Test
    void should_list_orders() throws Exception {
        // given:
        when(orderFacade.getAllOrders()).thenReturn(List.of(ORDER_1, ORDER_2));

        // when:
        var response = mockMvc.perform(get("/api/orders")).andReturn().getResponse();

        // then:
        assertThat(response).hasStatus(200);
        assertThat(response).hasContentType("application/json");
        assertThat(response).hasJsonBody(
            // language=json
            """
            [
                {
                    "orderId": "3745fd3b-62b1-40a1-ab32-57aa2ecf562f",
                    "clientId": "b74d2425-a3ad-4138-ae70-3c4ecbcf5803",
                    "orderStatus": "PENDING",
                    "date": "2025-07-10T15:00:00",
                    "items": []
                },
                {
                    "orderId": "605c2114-faaa-447d-adfd-582408389958",
                    "clientId": "85f30610-467c-49a1-a50a-9686aa6089dc",
                    "orderStatus": "PENDING",
                    "date": "2025-07-11T06:00:00",
                    "items": []
                }
            ]
            """
        );
    }



}
