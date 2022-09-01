package com.trip.flight.auto.compose.core;

import com.trip.flight.auto.compose.core.dependency.AutoComposableDependenciesCache;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Author wangzhuo
 * @Date: 2022/8/28 18:25
 */
public interface AutoComposable<T> {

    /**
     * 执行任务的实现方法
     *
     * @return
     */
    T execute();

    /**
     * 实际执行任务的模板方法
     *
     * @return
     */
    T executeTemplate(Optional<Stream<? extends T>> dependenciesOptional);

    /**
     * 执行任务的后置增强，可用于统一的异常处理等
     *
     * @return
     */
    default T postExecute(T t) {
        return t;
    }

    /**
     * 返回执行本任务需要依赖的其他任务
     * 需要注意：此方法用于收集需要执行的AutoComposable，所以方法中不能依赖任何其他AutoComposable执行后的数据。
     *
     * @return List
     */
    default List<? extends AutoComposable<T>> getDependentBeans() {
        return AutoComposableDependenciesCache.INSTANCE.getDependentBeans(this.getClass());
    }
}
