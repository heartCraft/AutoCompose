package com.trip.flight.auto.compose.core.reactor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

/**
 * @Author wangzhuo
 * @Date: 2022/8/29 10:16
 */
@RunWith(MockitoJUnitRunner.class)
public class ReactorAutoComposeUtilsTest {


    private Mono<Void> get0() {
        System.out.println("do get0");
        return Mono.just("get0").then();
    }

    private Mono<Void> get1() {
        System.out.println("do get1 after get0");
        return Mono.just("get1").then();
    }

    private Mono<Void> get2() {
        System.out.println("do get2 after get0");
        return Mono.just("get2").then();
    }

    private Mono<Void> get3() {
        System.out.println("do get3 after get1&get2");
        return Mono.just("get3").then();
    }

    private Mono<Void> convert() {
        System.out.println("convert get0 get1 get2 get3 data to response");
        return Mono.empty();
    }

    @Test
    public void test() {

        Mono<Void> get0 = Mono.empty().then(Mono.defer(() -> get0()).cache());

        Mono<Void> get1 = Mono.when(get0).then(Mono.defer(() -> get1()).cache());
        Mono<Void> get2 = Mono.when(get0).then(Mono.defer(() -> get2()).cache());

        Mono<Void> get3 = Mono.when(get1, get2).then(Mono.defer(() -> get3()).cache());

        Mono<Void> convert = Mono.when(get0, get1, get2, get3).then(Mono.defer(() -> convert()).cache());



        Mono.when(convert).then(Mono.defer(() -> {
            String response = "response";
            System.out.println("return response");
            return Mono.just(response);
        })).subscribe();


    }
}