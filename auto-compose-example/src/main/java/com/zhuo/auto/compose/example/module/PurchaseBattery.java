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
 * @Author wangzhuo
 * @Date: 2022/8/30 21:46
 */
@Component
@AutoComposableBeanDesc("采购电池")
public class PurchaseBattery implements ReactorAutoComposable<String> {

    @Autowired
    private RequestCarTypeHolder requestCarTypeHolder;

    @Override
    public Mono<String> execute() {
        String carType = requestCarTypeHolder.get();
        switch (carType) {
            case "model3":
                return Mono.just(carType)
                        .delayElement(Duration.ofSeconds(1))
                        .flatMap(s -> {
                            String battery = "（" + s + "）的电池";
                            // log
                            System.out.printf("%s %s: 耗时1s，采购%s%n",
                                    LocalDateTime.now(),
                                    Thread.currentThread().getName(),
                                    battery);
                            return Mono.just(battery);
                        });
            case "modelY":
                // log
                System.out.printf("%s %s:（%s）的电池缺货%n",
                        LocalDateTime.now(),
                        Thread.currentThread().getName(),
                        carType);
                return Mono.empty();
            default:
                // log
                System.out.printf("%s %s: 采购（%s）的电池失败%n",
                        LocalDateTime.now(),
                        Thread.currentThread().getName(),
                        carType);
                return Mono.error(new IllegalStateException("PurchaseBattery error"));
        }
    }
}
