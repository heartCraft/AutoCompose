package com.trip.flight.auto.compose.test;

import com.trip.flight.auto.compose.core.ReactorAutoComposable;
import com.trip.flight.auto.compose.core.scope.AsyncRequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author wangzhuo
 * @Date: 2022/8/30 22:40
 */
@AsyncRequestScope
public class ResponseBuilder implements ReactorAutoComposable {
    private String response;

    public String getResponse() {
        return response;
    }

    @Autowired
    private Map<String, ReactorAutoComposableBuildable> builderMap;

    @Override
    public Mono<Void> execute() {
        System.out.println("ResponseBuilder");
        response = builderMap.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue().getResult())
                .collect(Collectors.toList())
                .toString();
        return Mono.empty();
    }
}
