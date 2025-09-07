package com.ecmsp.orderservice.order.adapter.repository.generator;

import com.ecmsp.orderservice.order.adapter.generator.GeneratorFacade;
import com.ecmsp.orderservice.order.domain.CorrelationId;
import com.ecmsp.orderservice.order.domain.OrderId;
import com.ecmsp.orderservice.order.domain.OrderIdGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class GeneratorFacadeTest {

    private static final CorrelationId FIXED_CORRELATION_ID = new CorrelationId(UUID.fromString("456789ab-cdef-1234-5678-90abcdef1234"));
    private static GeneratorFacade generatorFacade;

    @BeforeAll
    static void setup() {
        generatorFacade = new GeneratorFacade();
    }

    @Test
    void should_generate_random_order_id_successfully() {
        // given:
        OrderIdGenerator randomOrderIdGenerator = generatorFacade.getRandomOrderIdGenerator();
        // when:
        OrderId orderId = randomOrderIdGenerator.generate(FIXED_CORRELATION_ID);

        // then:
        assertThat(orderId).isNotNull();
        assertThat(orderId.value()).isNotNull();
        assertThat(orderId.value()).isInstanceOf(java.util.UUID.class);

    }


    @Test
    void should_generate_fixed_order_id_successfully() {
        // given:
        OrderIdGenerator fixedOrderIdGenerator = generatorFacade.getFixedOrderIdGenerator();
        // when:
        OrderId orderId = fixedOrderIdGenerator.generate(FIXED_CORRELATION_ID);
        // then:
        assertThat(orderId).isEqualTo(new OrderId(FIXED_CORRELATION_ID.value()));

    }

    @Test
    void should_throw_exception_when_fixed_order_id_generator_with_null_correlation_id() {
        // given:
        OrderIdGenerator fixedOrderIdGenerator = generatorFacade.getFixedOrderIdGenerator();
        // when & then:
        assertThatThrownBy(() -> fixedOrderIdGenerator.generate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CorrelationId is required for fixed order ID generation");

    }






}
