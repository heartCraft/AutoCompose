package com.zhuo.auto.compose.core.dataholder;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangzhuo
 * @date 2024/2/2 11:10
 */
public class TransmittableThreadLocalDataHolder implements DataHolder {

    // 使用ttl缓存组件执行结果（参考文档：https://github.com/alibaba/transmittable-thread-local）
    // 创建子线程时不把父线程的数据传递给子线程
    private static final TransmittableThreadLocal<Map<Class, Object>> TTL =
            TransmittableThreadLocal.withInitialAndCopier(() -> new ConcurrentHashMap<>(64),
                    parentValue -> null,
                    parentValue -> parentValue);

    /**
     * 清空缓存数据。在每次请求处理前后都清除，建议通过Filter实现
     */
    public static void reset() {
        TTL.remove();
    }

    /**
     * 获取数据
     *
     * @param clazz
     * @return
     */
    public <R> R getResult(Class clazz) {
        return (R) TTL.get().get(clazz);
    }

    /**
     * 存储数据
     *
     * @param clazz
     * @param result
     * @return
     */
    public void cacheResult(Class clazz, Object result) {
        if (result == null) {
            return;
        }
        TTL.get().put(clazz, result);
    }

    @Override
    public Object getCacheObject() {
        return TTL.get();
    }
}
