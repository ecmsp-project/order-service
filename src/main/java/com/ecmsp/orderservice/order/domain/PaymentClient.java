package com.ecmsp.orderservice.order.domain;

public interface PaymentClient {
    void createPayment(PaymentToCreate paymentToCreate);
}
