package com.ecmsp.orderservice.e2e.utils;

import okhttp3.OkHttpClient;

public class OrderServiceApi {
    private final OkHttpClient client;



    public OrderServiceApi(OkHttpClient client) {
        this.client = client;
    }


}
