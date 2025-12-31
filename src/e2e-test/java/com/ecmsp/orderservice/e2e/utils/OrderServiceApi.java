package com.ecmsp.orderservice.e2e.utils;

import com.ecmsp.order.v1.GetOrderRequest;
import com.ecmsp.order.v1.GetOrderResponse;
import com.ecmsp.order.v1.OrderServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.net.URI;

public class OrderServiceApi {
    private final OrderServiceGrpc.OrderServiceBlockingStub blockingStub;
    private final ManagedChannel channel;

    public OrderServiceApi(URI uri) {
        this.channel = ManagedChannelBuilder.forAddress(uri.getHost(), uri.getPort())
                .usePlaintext()
                .build();
        this.blockingStub = OrderServiceGrpc.newBlockingStub(channel);

    }

    public void shutdown() {
        channel.shutdown();
    }

    public GetOrderResponse getOrderById(String orderId) {
        GetOrderRequest request = GetOrderRequest.newBuilder()
                .setOrderId(orderId)
                .build();
        GetOrderResponse response = blockingStub.getOrder(request);
        System.out.println("GetOrderResponse: " + response);
        return response;
    }



}
