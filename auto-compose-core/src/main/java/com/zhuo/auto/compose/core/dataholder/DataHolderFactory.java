package com.zhuo.auto.compose.core.dataholder;

/**
 * @author :dssun
 * @date : 2024/7/8
 */
public class DataHolderFactory {

    private static volatile DataHolder dataHolder = null;

    /**
     * 注册自定义缓存数据实现
     *
     * @param holder
     */
    public static void registerDataHolder(DataHolder holder) {
        dataHolder = holder;
    }

    /**
     * 获取缓存数据实现
     *
     * @return
     */
    public static DataHolder getDataHolder() {
        if (dataHolder == null) {
            synchronized (DataHolderFactory.class) {
                if (dataHolder == null) {
                    dataHolder = new TransmittableThreadLocalDataHolder();
                }
            }
        }
        return dataHolder;
    }

}
