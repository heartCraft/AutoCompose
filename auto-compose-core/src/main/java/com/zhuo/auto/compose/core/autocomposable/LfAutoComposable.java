package com.zhuo.auto.compose.core.autocomposable;

import com.zhuo.auto.compose.core.dataholder.ResultHolder;
import com.zhuo.auto.compose.core.exception.LostDataHolderException;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用ListenableFuture框架异步编程，组件均需实现此接口
 *
 * @Author wangzhuo
 * @Date: 2022/8/30 23:33
 */
public interface LfAutoComposable<R> extends AutoComposable<ListenableFuture<R>, R> {

    /**
     * 实际执行内容
     *
     * @return 异步执行结果
     */
    ListenableFuture<R> execute();

    default ListenableFuture<R> enhanceExecuteResult(ListenableFuture<R> lf) {
        return lf;
    }

    @Override
    default ListenableFuture<R> executeTemplate(Optional<Stream<? extends ListenableFuture<R>>> dependenciesOptional,
            Object cacheData) {
        ListenableFuture<?> dependenciesCf = dependenciesOptional
                .map(stream -> stream.collect(Collectors.toList()))
                .map(Futures::allAsList)
                .orElse(Futures.immediateFuture(null));
        ListenableFuture<R> executeResultLf = Futures.transformAsync(dependenciesCf,
                (input) -> this.execute(),
                MoreExecutors.directExecutor());
        return Futures.transform(enhanceExecuteResult(executeResultLf),
                (result) -> {
                    if (ResultHolder.getCacheObject() != cacheData) {
                        throw new LostDataHolderException(this.getClass());
                    }
                    ResultHolder.cacheResult(this.getClass(), result);
                    return result;
                },
                MoreExecutors.directExecutor());
    }

    @Override
    default ListenableFuture<R> getDefaultResult() {
        return Futures.immediateFuture(null);
    }


    /**
     * 使用ListenableFuture框架异步编程, 同步执行的组件可实现此接口
     *
     * @param <R> 执行结果类型
     */
    interface Sync<R> extends LfAutoComposable<R> {
        @Override
        default ListenableFuture<R> execute() {
            return Futures.immediateFuture(this.syncExecute());
        }

        /**
         * 同步执行内容
         *
         * @return 同步执行结果
         */
        R syncExecute();
    }
}
