package com.zhuo.auto.compose.example;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.zhuo.auto.compose.core.AutoComposeUtils;
import com.zhuo.auto.compose.core.dataholder.TransmittableThreadLocalDataHolder;
import com.zhuo.auto.compose.core.dependency.AutoComposableDependenciesCache;
import com.zhuo.auto.compose.example.module.MakeCarEnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

@SpringBootApplication
public class AutoComposeApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AutoComposeApplication.class, args);
        System.setProperty("java.awt.headless", "false");

        // 控制台输出依赖关系
        AutoComposableDependenciesCache.INSTANCE.generateDependenciesString();
        // 生成依赖关系图片
        AutoComposableDependenciesCache.INSTANCE.generateDependenciesImg();


        Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080/?carType=model3"));
        Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080/?carType=modelY"));
        Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080/?carType=modelS"));

    }


    @Autowired
    private AutoComposeUtils autoComposeUtils;

    @Autowired
    private MakeCarEnd makeCarEnd;

    @Autowired
    private RequestCarTypeHolder requestCarTypeHolder;


    @RestController
    public class TestController {
        @GetMapping(value = "", produces = MediaType.ALL_VALUE)
        public Mono<String> test(@RequestParam(defaultValue = "") String carType) {
            // 存储上下文数据到AutoCompose框架中
            requestCarTypeHolder.set(carType);

            // log
            System.out.printf("%n%s %s: 开始生产%s汽车%n",
                    LocalDateTime.now(),
                    Thread.currentThread().getName(),
                    carType);

            // 执行自动编排，入参为根结点bean
            return autoComposeUtils.execute(makeCarEnd)
                    .onErrorResume(throwable -> {
                        // log
                        System.out.printf("%s %s: %s生产失败，原因：%s%n%n",
                                LocalDateTime.now(),
                                Thread.currentThread().getName(),
                                carType,
                                throwable.getMessage());
                        return Mono.just(carType + "生产失败");
                    })
                    .subscribeOn(Schedulers.fromExecutor(ForkJoinPool.commonPool()));
        }
    }


    {
        // 使用Reactor框架的线程池装饰扩展方法，给Reactor的线程池都装饰上TTL的跨线程ThreadLocal传递功能
        Schedulers.addExecutorServiceDecorator("_TTL",
                (scheduler, scheduledExecutorService) -> TtlExecutors.getTtlScheduledExecutorService(
                        scheduledExecutorService));
    }

    @Component
    public static class AutoComposeFilter implements WebFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            // 清除线程中缓存bean对象
            TransmittableThreadLocalDataHolder.reset();
            return chain.filter(exchange);
        }
    }
}
