package com.zhuo.auto.compose.core.autocomposable;

import com.zhuo.auto.compose.core.dataholder.ResultHolder;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 组件 == 实现了AutoComposable接口的Bean对象。
 *
 * <p>需要根据编程框架对executeTemplate方法做对应的实现。
 * 默认提供了：
 * 1. {@link SyncAutoComposable}用于同步编程
 * 2. {@link CfAutoComposable}用于CompletableFuture异步编程
 * 3. {@link ReactorAutoComposable}用于Reactor异步编程
 * 4. {@link LfAutoComposable}用于ListenableFuture异步编程
 *
 * @Author wangzhuo
 * @Date: 2022/8/28 18:25
 */
public interface AutoComposable<T, R> {

    /**
     * 执行的模板方法，需要根据编程框架做对应的实现
     *
     * @return 是否执行完成的结果
     */
    T executeTemplate(Optional<Stream<? extends T>> dependenciesOptional, Object cacheData);

    /**
     * 获取默认的返回结果
     *
     * @return 默认的执行完成结果
     */
    T getDefaultResult();

    /**
     * 获取组件执行结果数据
     *
     * @return 执行结果数据
     */
    default R getExecuteResult() {
        return ResultHolder.getResult(this.getClass());
    }

    /**
     * 用于关闭当前组件及其依赖组件的执行
     *
     * <p>注意：此方法是在所有组件执行前调用，所以方法中不能使用任何组件执行后的数据。
     *
     * @return 返回true标识关闭当前组件及其依赖组件的执行，默认返回fasle
     */
    default boolean close() {
        return false;
    }
}
