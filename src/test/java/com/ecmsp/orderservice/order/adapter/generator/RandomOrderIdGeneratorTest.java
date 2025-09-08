package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RandomOrderIdGeneratorTest {
    private static final CorrelationId FIXED_CORRELATION_ID = new CorrelationId(UUID.fromString("456789ab-cdef-1234-5678-90abcdef1234"));

    @Test
    void should_generate_random_order_id_successfully() {
        // given:
        OrderIdGenerator randomOrderIdGenerator = new RandomOrderIdGenerator();
        // when:
        OrderId orderId = randomOrderIdGenerator.generate(FIXED_CORRELATION_ID);
        // then:
        assertThat(orderId).isNotNull();
        assertThat(orderId.value()).isNotNull();
        assertThat(orderId.value()).isInstanceOf(java.util.UUID.class);
    }
}
