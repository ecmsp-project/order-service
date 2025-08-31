package com.ecmsp.orderservice.order.adapter.payment.inmemory;

import com.ecmsp.orderservice.order.domain.PaymentClient;
import com.ecmsp.orderservice.order.domain.PaymentToCreate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class InMemoryPaymentClient implements PaymentClient {

    @Override
    public void createPayment(PaymentToCreate paymentToCreate) {
        log.info("Payment created for orderId `{}` and clientId `{}` with amount `{}`",
            paymentToCreate.orderId().value(),
            paymentToCreate.clientId().value(),
            paymentToCreate.amount()
        );
    }
}
