package com.ecmsp.orderservice.application.security.grpc;

import com.ecmsp.orderservice.application.security.UserContextData;
import io.grpc.Context;

public class UserContextGrpcHolder {

    public static final Context.Key<UserContextData> USER_CONTEXT_KEY = Context.key("user-context");

    public static UserContextData getUserContext() {
        return USER_CONTEXT_KEY.get();
    }
}
