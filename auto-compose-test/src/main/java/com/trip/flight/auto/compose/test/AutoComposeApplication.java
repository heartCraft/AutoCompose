package com.trip.flight.auto.compose.test;

import com.trip.flight.auto.compose.core.AutoComposableUtils;
import com.trip.flight.auto.compose.ttl.request.scope.TtlBeanCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class AutoComposeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoComposeApplication.class, args);
    }


    @Autowired
    private AutoComposableUtils autoComposableUtils;

    @Autowired
    private ResponseBuilder responseBuilder;



    @RestController
    class EmployeeController {
        @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Mono<String> greeting() {
            TtlBeanCache.clear();
            return autoComposableUtils.execute(responseBuilder)
                    .then(Mono.defer(() -> Mono.just(responseBuilder.getResponse())));
        }
    }


}
