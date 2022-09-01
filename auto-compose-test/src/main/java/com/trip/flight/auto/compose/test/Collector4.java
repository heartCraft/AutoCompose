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
public class Collector4 implements ReactorAutoComposable {

    @Autowired
    private Collector2 collector2;

    @Autowired
    private Collector3 collector3;

    private String data;

    public String getData() {
        return data;
    }

    @Override
    public Mono<Void> execute() {
        System.out.println("Collector4");
        return Mono.just("data4")
                .doOnNext(s -> data = s + "(" + collector2.getData() + "," + collector3.getData() + ")")
                .then();
    }
}
