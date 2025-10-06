package com.ecmsp.orderservice.order.domain;

import java.util.UUID;

public record VariantId(UUID value) {

    @Override
    public String toString(){
        return value.toString();
    }

}
