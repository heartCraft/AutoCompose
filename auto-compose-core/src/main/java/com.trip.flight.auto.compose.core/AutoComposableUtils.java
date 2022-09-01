package com.trip.flight.auto.compose.core;

import com.trip.flight.auto.compose.core.exception.CycleDependencyException;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @Author wangzhuo
 * @Date: 2022/8/31 10:05
 */
public class AutoComposableUtils {

    /**
     * 执行自动编排
     *
     * @param autoComposable 指定自动编排的根任务
     * @return
     */
    public <T> T execute(AutoComposable<T> autoComposable) {
        Objects.requireNonNull(autoComposable);

        Map<AutoComposable<T>, T> executedMap = new HashMap<>();
        Set<AutoComposable<T>> executingSet = new HashSet<>();
        return execute(autoComposable, executedMap, executingSet);
    }

    /**
     * 获取指定任务的结果
     *
     * @param autoComposable 指定的任务
     * @param executedMap    已执行任务及结果的缓存Map
     * @param executingSet   执行中任务Set，用于循环依赖检测
     * @return
     */
    private <T> T execute(AutoComposable<T> autoComposable,
                          Map<AutoComposable<T>, T> executedMap,
                          Set<AutoComposable<T>> executingSet) {
        // 当前任务已执行，则使用缓存结果
        if (executedMap.containsKey(autoComposable)) {
            return executedMap.get(autoComposable);
        }

        // 执行当前任务需要依赖的其他任务
        List<? extends AutoComposable<T>> autoComposableDependencies = autoComposable.getDependentBeans();
        if (!CollectionUtils.isEmpty(autoComposableDependencies)) {
            // 有循环依赖时抛异常
            if (autoComposableDependencies.stream().anyMatch(executingSet::contains)) {
                throw new CycleDependencyException(autoComposable);
            }
        }

        // 记录当前任务至处理中任务Set，用于循环依赖检测
        executingSet.add(autoComposable);

        // 获取所有依赖任务的执行结果后，执行当前任务
        T t = autoComposable.executeTemplate(Optional.ofNullable(autoComposableDependencies)
                .map(List::stream)
                .map(stream -> stream.map(dependency -> execute(dependency, executedMap, executingSet))));

        // 从处理中任务Set清除当前任务
        executingSet.remove(autoComposable);

        // 缓存当前任务的执行结果后返回
        executedMap.put(autoComposable, t);
        return t;
    }
}
