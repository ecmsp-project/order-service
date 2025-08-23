package com.ecmsp.orderservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;

public class MockMvcAssertionsUtils {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MockMvcAssertionsUtils() {}

    public static MockHttpServletResponseAssert assertThat(MockHttpServletResponse response) {
        return new MockHttpServletResponseAssert(response);
    }

}
