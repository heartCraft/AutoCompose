package com.zhuo.auto.compose.core.autocomposable;

import com.zhuo.auto.compose.core.dataholder.ResultHolder;
import com.zhuo.auto.compose.core.exception.LostDataHolderException;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * 同步编程，组件均需实现此接口
 *
 * @Author wangzhuo
 * @Date: 2022/9/1 21:38
 */
public interface SyncAutoComposable<R> extends AutoComposable<CompletableFuture<R>, R> {

    /**
     * 实际执行内容
     */
    R execute();

    /**
     * 执行本任务的Executor
     *
     * @return
     */
    default Executor getExecutor() {
        return MoreExecutors.directExecutor();
    }

    @Override
    default CompletableFuture<R> executeTemplate(Optional<Stream<? extends CompletableFuture<R>>> dependenciesOptional,
            Object cacheData) {
        CompletableFuture<Void> dependenciesCf = dependenciesOptional
                .map(stream -> stream.toArray(CompletableFuture[]::new))
                .map(CompletableFuture::allOf)
                .orElse(CompletableFuture.completedFuture(null));
        return dependenciesCf
                .thenCompose(v -> CompletableFuture.supplyAsync(this::execute, getExecutor())
                        .thenApply(result -> {
                            if (ResultHolder.getCacheObject() != cacheData) {
                                throw new LostDataHolderException(this.getClass());
                            }
                            ResultHolder.cacheResult(this.getClass(), result);
                            return result;
                        }));
    }

    @Override
    default CompletableFuture<R> getDefaultResult() {
        return CompletableFuture.completedFuture(null);
    }
}
