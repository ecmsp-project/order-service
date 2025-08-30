package com.ecmsp.orderservice.e2e.tests;

import com.ecmsp.orderservice.e2e.E2ETestEnvironment;
import org.junit.jupiter.api.Test;

import static com.ecmsp.orderservice.e2e.E2ETestEnvironment.Containers.ORDER_SERVICE;

public class SampleE2ETest {

    @Test
    public void should_pass() {
        System.out.println("order-service run at: " + E2ETestEnvironment.getUrl(ORDER_SERVICE));
    }

}
