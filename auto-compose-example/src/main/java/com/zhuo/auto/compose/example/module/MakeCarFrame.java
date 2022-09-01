package com.zhuo.auto.compose.example.module;

import com.zhuo.auto.compose.core.annotation.AutoComposableBeanDesc;
import com.zhuo.auto.compose.core.autocomposable.ReactorAutoComposable;
import com.zhuo.auto.compose.example.RequestCarTypeHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author wangzhuo
 * @date 2024/1/7 22:16
 */
@Component
@AutoComposableBeanDesc("制造车架")
public class MakeCarFrame implements ReactorAutoComposable<String> {

    @Autowired
    private RequestCarTypeHolder requestCarTypeHolder;

    @Override
    public Mono<String> execute() {
        return Mono.just(requestCarTypeHolder.get())
                .delayElement(Duration.ofSeconds(1))
                .map(s -> {
                    String carFrame = "（" + s + "）车架";
                    // log
                    System.out.printf("%s %s: 耗时1s，制造%s%n",
                            LocalDateTime.now(),
                            Thread.currentThread().getName(),
                            carFrame);
                    return carFrame;
                });


    }

}
