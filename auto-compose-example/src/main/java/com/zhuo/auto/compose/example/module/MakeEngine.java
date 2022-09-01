package com.zhuo.auto.compose.example.module;

import com.zhuo.auto.compose.core.annotation.AutoComposableBeanDesc;
import com.zhuo.auto.compose.core.autocomposable.ReactorAutoComposable;
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
@AutoComposableBeanDesc("制造引擎")
public class MakeEngine implements ReactorAutoComposable<String> {

    @Autowired
    private PurchaseBattery purchaseBattery;


    @Override
    public Mono<String> execute() {
        return Mono.justOrEmpty(purchaseBattery.getExecuteResult())
                .delayElement(Duration.ofSeconds(1))
                .map(battery -> {
                    String engine = "适配（" + battery + "）的引擎";
                    // log
                    System.out.printf("%s %s: 耗时1s，采购电池后，制造%s%n",
                            LocalDateTime.now(),
                            Thread.currentThread().getName(),
                            engine);
                    return engine;
                });
    }
}
