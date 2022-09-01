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
public class Collector3 implements ReactorAutoComposable {

    @Autowired
    private Collector1 collector1;

    private String data;

    public String getData() {
        return data;
    }

    @Override
    public Mono<Void> execute() {
        System.out.println("Collector3");
        return Mono.just("data3")
                .doOnNext(s -> data = s + "(" + collector1.getData() + ")")
                .then();
    }
}
