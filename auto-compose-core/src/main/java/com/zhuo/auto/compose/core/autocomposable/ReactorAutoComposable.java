package com.zhuo.auto.compose.core.autocomposable;

import com.zhuo.auto.compose.core.dataholder.ResultHolder;
import com.zhuo.auto.compose.core.exception.LostDataHolderException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用Reactor框架异步编程，组件均需实现此接口
 *
 * @Author wangzhuo
 * @Date: 2022/8/28 18:37
 */
public interface ReactorAutoComposable<R> extends AutoComposable<Mono<R>, R> {

    /**
     * 实际执行内容
     *
     * @return 异步执行结果
     */
    Mono<R> execute();

    default Mono<R> enhanceExecuteResult(Mono<R> mono) {
        return mono;
    }

    @Override
    default Mono<R> executeTemplate(Optional<Stream<? extends Mono<R>>> dependenciesOptional, Object cacheData) {
        Mono<Void> dependenciesMono = dependenciesOptional
                .map(stream -> stream.collect(Collectors.toList()))
                .map(Mono::when)
                .orElse(Mono.empty());
        Mono<R> executeResultMono = dependenciesMono.then(Mono.defer(this::execute));
        return enhanceExecuteResult(executeResultMono)
                .doOnNext(result -> {
                    if (ResultHolder.getCacheObject() != cacheData) {
                        throw new LostDataHolderException(this.getClass());
                    }
                    ResultHolder.cacheResult(this.getClass(), result);
                })
                .cache();
    }

    @Override
    default Mono<R> getDefaultResult() {
        return Mono.empty();
    }


    /**
     * 使用Reactor框架异步编程, 同步执行的组件可实现此接口
     *
     * @param <R> 执行结果类型
     */
    interface Sync<R> extends ReactorAutoComposable<R> {
        @Override
        default Mono<R> execute() {
            return Mono.justOrEmpty(this.syncExecute());
        }

        /**
         * 同步执行内容
         *
         * @return 同步执行结果
         */
        R syncExecute();
    }
}
