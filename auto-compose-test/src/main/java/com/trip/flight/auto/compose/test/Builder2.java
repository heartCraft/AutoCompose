package com.trip.flight.auto.compose.test;

import com.trip.flight.auto.compose.core.scope.AsyncRequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

/**
 * @Author wangzhuo
 * @Date: 2022/8/30 23:10
 */
@AsyncRequestScope
public class Builder2 implements ReactorAutoComposableBuildable {

    private String result;

    @Override
    public String getResult() {
        return result;
    }


    @Autowired
    private Collector2 collector2;


    @Override
    public Mono<Void> execute() {
        System.out.println("Builder2");
        result = collector2.getData();
        return Mono.empty();
    }
}
