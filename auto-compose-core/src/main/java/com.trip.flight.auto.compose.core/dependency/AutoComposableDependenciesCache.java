package com.trip.flight.auto.compose.core.dependency;


import com.trip.flight.auto.compose.core.AutoComposable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author wangzhuo
 * @Date: 2022/8/28 21:55
 */
public class AutoComposableDependenciesCache<T> {
    // 饿汉式单例
    public static final AutoComposableDependenciesCache INSTANCE = new AutoComposableDependenciesCache();

    // 缓存依赖关系的map
    private Map<Class<? extends AutoComposable<T>>, List<? extends AutoComposable<T>>> cacheMap;

    /**
     * 获取依赖的实现了AutoComposable接口的bean集合
     *
     * @param c 指定AutoComposable实现类
     * @return
     */
    public List<? extends AutoComposable<T>> getDependentBeans(Class<? extends AutoComposable<T>> c) {
        Objects.requireNonNull(c);
        return cacheMap.get(c);
    }

    /**
     * TODO
     * 获取所有的依赖关系，做可视化展示
     *
     * @return
     */
    public Map<Class<? extends AutoComposable<T>>, List<? extends AutoComposable<T>>> getDependentBeans() {
        return cacheMap;
    }

    /**
     * 初始化缓存map
     *
     * @param size
     */
    void initCacheMap(int size) {
        if (cacheMap == null) {
            cacheMap = new HashMap<>((int) (size / 0.75 + 1));
        }
    }

    /**
     * 缓存依赖关系
     *
     * @param c              指定AutoComposable实现类
     * @param dependentBeans 依赖的实现了AutoComposable接口的bean集合
     */
    void cacheDependentBeans(Class<? extends AutoComposable<T>> c, List<? extends AutoComposable<T>> dependentBeans) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(dependentBeans);
        cacheMap.put(c, dependentBeans);
    }

    private AutoComposableDependenciesCache() {
    }
}
