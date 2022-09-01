package com.trip.flight.auto.compose.test;

import com.trip.flight.auto.compose.core.scope.AsyncRequestScope;

/**
 * @Author wangzhuo
 * @Date: 2022/8/31 15:31
 */
@AsyncRequestScope
public class RequestContext {
    private String request;

    public boolean init(String request) {
        this.request = request;
        return true;
    }

    public String getRequest() {
        return request;
    }
}
