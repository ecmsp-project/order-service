package com.ecmsp.orderservice.order.domain.returns;

import com.ecmsp.orderservice.order.domain.ClientId;
import com.ecmsp.orderservice.order.domain.OrderException;

import java.util.List;
import java.util.Optional;

public interface ReturnFacade {
    ReturnOrder createReturn(ReturnToCreate returnToCreate) throws OrderException.OrderNotReturnable;
    Optional<ReturnOrder> findReturnById(ReturnId returnId);
    List<ReturnOrder> getAllReturns();
    List<ReturnOrder> getReturnsByUserId(ClientId clientId);
}
