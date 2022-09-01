package com.trip.flight.auto.compose.ttl.request.scope;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author wangzhuo
 * @Date: 2022/8/28 15:45
 */
public class TtlBeanCache {

    // 使用ttl缓存bean对象（参考文档：https://github.com/alibaba/transmittable-thread-local）
    private static final TransmittableThreadLocal<Map<String, Object>> TTL = TransmittableThreadLocal.withInitial(() -> new ConcurrentHashMap<>(64));

    public static Object getBean(String name, ObjectFactory<?> objectFactory) {
        return TTL.get().computeIfAbsent(name, v -> objectFactory.getObject());
    }

    public static void clear() {
        TTL.remove();
    }
}
