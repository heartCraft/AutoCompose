package com.zhuo.auto.compose.core.autocomposable;

import com.zhuo.auto.compose.core.dataholder.ResultHolder;
import com.zhuo.auto.compose.core.exception.LostDataHolderException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 使用CompletableFuture框架异步编程，组件均需实现此接口
 *
 * @Author wangzhuo
 * @Date: 2022/8/30 23:33
 */
public interface CfAutoComposable<R> extends AutoComposable<CompletableFuture<R>, R> {

    /**
     * 实际执行内容
     *
     * @return 异步执行结果
     */
    CompletableFuture<R> execute();

    default CompletableFuture<R> enhanceExecuteResult(CompletableFuture<R> cf) {
        return cf;
    }

    @Override
    default CompletableFuture<R> executeTemplate(Optional<Stream<? extends CompletableFuture<R>>> dependenciesOptional,
            Object cacheData) {
        CompletableFuture<Void> dependenciesCf = dependenciesOptional
                .map(stream -> stream.toArray(CompletableFuture[]::new))
                .map(CompletableFuture::allOf)
                .orElse(CompletableFuture.completedFuture(null));
        CompletableFuture<R> executeResultCf = dependenciesCf
                .thenCompose(v -> this.execute());
        return enhanceExecuteResult(executeResultCf)
                .thenApply(result -> {
                    if (ResultHolder.getCacheObject() != cacheData) {
                        throw new LostDataHolderException(this.getClass());
                    }
                    ResultHolder.cacheResult(this.getClass(), result);
                    return result;
                });
    }

    @Override
    default CompletableFuture<R> getDefaultResult() {
        return CompletableFuture.completedFuture(null);
    }


    /**
     * 使用CompletableFuture框架异步编程, 同步执行的组件可实现此接口
     *
     * @param <R> 执行结果类型
     */
    interface Sync<R> extends CfAutoComposable<R> {
        @Override
        default CompletableFuture<R> execute() {
            return CompletableFuture.completedFuture(this.syncExecute());
        }

        /**
         * 同步执行内容
         *
         * @return 同步执行结果
         */
        R syncExecute();
    }
}
