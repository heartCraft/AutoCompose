package com.trip.flight.auto.compose.test;

import com.trip.flight.auto.compose.core.ReactorAutoComposable;
import com.trip.flight.auto.compose.core.scope.AsyncRequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

/**
 * @Author wangzhuo
 * @Date: 2022/8/30 21:46
 */
@AsyncRequestScope
public class Collector1 implements ReactorAutoComposable {

    private String data;

    public String getData() {
        return data;
    }

    @Autowired
    private RequestContext requestContext;

    @Override
    public Mono<Void> execute() {
        System.out.println("Collector1");
        return Mono.just("data1")
                .doOnNext(s -> data = s + "(" + requestContext.getRequest() + ")")
                .then();
    }
}
