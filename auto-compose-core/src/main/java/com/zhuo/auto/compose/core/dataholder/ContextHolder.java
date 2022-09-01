package com.zhuo.auto.compose.core.dataholder;

/**
 * 上下文数据传递接口，默认实现了上下文数据存储和获取。
 *
 * @author :dssun
 * @date : 2024/5/15
 */
public interface ContextHolder<T> {
    /**
     * 存储上下文数据
     *
     * @param data 上下文数据
     */
    default void set(T data) {
        ResultHolder.cacheResult(this.getClass(), data);
    }

    /**
     * 获取上下文数据
     *
     * @return 上下文数据
     */
    default T get() {
        return ResultHolder.getResult(this.getClass());
    }
}
