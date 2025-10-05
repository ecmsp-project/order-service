package com.ecmsp.orderservice.order.adapter.generator;

import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.returns.ReturnId;
import com.ecmsp.orderservice.order.domain.returns.ReturnIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RandomReturnIdGeneratorTest {
    private static final CorrelationId FIXED_CORRELATION_ID = new CorrelationId(UUID.fromString("456789ab-cdef-1234-5678-90abcdef1234"));

    @Test
    void should_generate_random_return_id_successfully() {
        // given:
        ReturnIdGenerator randomReturnIdGenerator = new RandomReturnIdGenerator();
        // when:
        ReturnId returnId = randomReturnIdGenerator.generate(FIXED_CORRELATION_ID);
        // then:
        assertThat(returnId).isNotNull();
        assertThat(returnId.id()).isNotNull();
        assertThat(returnId.id()).isInstanceOf(java.util.UUID.class);
    }
}
