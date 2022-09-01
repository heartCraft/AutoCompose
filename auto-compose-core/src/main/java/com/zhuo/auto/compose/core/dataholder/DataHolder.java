package com.zhuo.auto.compose.core.dataholder;

/**
 * @author :dssun
 * @date : 2024/7/8
 */
public interface DataHolder {

    /**
     * 获取数据
     *
     * @param clazz
     * @return
     */
    <R> R getResult(Class clazz);

    /**
     * 存储数据
     *
     * @param clazz
     * @param result
     * @return
     */
    void cacheResult(Class clazz, Object result);

    /**
     * 获取缓存对象，用于判断执行过程中缓存是否一致
     *
     * @return
     */
    Object getCacheObject();
}
