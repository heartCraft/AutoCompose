package com.zhuo.auto.compose.core.dataholder;

/**
 * @author wangzhuo
 * @date 2024/2/2 11:10
 */
public class ResultHolder {
    /**
     * 获取数据
     *
     * @param clazz
     * @return
     */
    public static <R> R getResult(Class clazz) {
        return DataHolderFactory.getDataHolder().getResult(clazz);
    }

    /**
     * 存储数据
     *
     * @param clazz
     * @param result
     * @return
     */
    public static void cacheResult(Class clazz, Object result) {
        DataHolderFactory.getDataHolder().cacheResult(clazz, result);
    }

    /**
     * 存储数据
     *
     * @return
     */
    public static Object getCacheObject() {
        return DataHolderFactory.getDataHolder().getCacheObject();
    }
}
