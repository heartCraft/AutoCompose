package com.zhuo.auto.compose.core;

import com.zhuo.auto.compose.core.autocomposable.AutoComposable;
import com.zhuo.auto.compose.core.autocomposable.SyncAutoComposable;
import com.zhuo.auto.compose.core.dataholder.ResultHolder;
import com.zhuo.auto.compose.core.dependency.AutoComposableDependenciesCache;
import com.zhuo.auto.compose.core.exception.CycleDependencyException;
import com.zhuo.auto.compose.core.exception.MissDataHolderResetException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * 自动编排执行工具类，唯一的入口方法为execute(AutoComposable rootAutoComposable)
 *
 * @Author wangzhuo
 * @Date: 2022/8/31 10:05
 */
public class AutoComposeUtils {

    /**
     * 从指定根节点组件后序遍历，自动编排执行依赖路径上的所有组件。
     *
     * <p>组件 == 实现了AutoComposable接口的Bean对象。
     * 所有组件为节点，依赖关系为有向边，构成DAG(Directed Acyclic Graph)有向无环图。
     * 后序遍历（先叶子节点，后父节点）即可控制依赖路径上的**被依赖组件先执行，依赖组件后执行**，从而实现所有组件的有序执行。
     *
     * @param rootAutoComposable 根节点组件
     * @return 是否执行完成的结果
     */
    public <T, R> T execute(AutoComposable<T, R> rootAutoComposable) {
        Objects.requireNonNull(rootAutoComposable);

        // 校验是否已清理组件执行结果缓存。如未清理，上次执行结果缓存会影响本次执行结果，造成错误。
        if (ResultHolder.getResult(rootAutoComposable.getClass()) != null) {
            throw new MissDataHolderResetException();
        }

        // 已执行的组件及其结果的缓存Map
        Map<AutoComposable<T, R>, T> executedMap = new HashMap<>();
        // 执行中的组件缓存Set，用于循环依赖检测
        Set<AutoComposable<T, R>> executingSet = new HashSet<>();
        return execute(rootAutoComposable, executedMap, executingSet);
    }

    /**
     * 供SyncAutoComposable使用的特殊execute方法，将CompletableFuture结果转为同步结果
     *
     * @param rootAutoComposable
     * @param <R>
     * @return
     */
    public <R> R execute(SyncAutoComposable<R> rootAutoComposable) {
        return execute((AutoComposable<CompletableFuture<R>, R>) rootAutoComposable).join();
    }

    /**
     * 执行指定的组件，返回其是否执行完成的结果
     *
     * @param autoComposable 指定执行的组件
     * @param executedMap    已执行的组件及其结果的缓存Map
     * @param executingSet   执行中的组件缓存Set，用于循环依赖检测
     * @return 是否执行完成的结果
     */
    private <T, R> T execute(AutoComposable<T, R> autoComposable,
            Map<AutoComposable<T, R>, T> executedMap,
            Set<AutoComposable<T, R>> executingSet) {
        // 当前组件已执行，则返回缓存的结果
        if (executedMap.containsKey(autoComposable)) {
            return executedMap.get(autoComposable);
        }

        // 控制当前组件及其依赖的组件集合是否执行，如果不执行，缓存默认结果并返回
        if (autoComposable.close()) {
            T result = autoComposable.getDefaultResult();
            executedMap.put(autoComposable, result);
            return result;
        }

        // 获取当前组件依赖的组件集合
        Set<? extends AutoComposable<T, R>> autoComposableDependencies
                = AutoComposableDependenciesCache.INSTANCE.getDependentBeans(autoComposable.getClass());

        // 有循环依赖时抛异常
        if (Optional.ofNullable(autoComposableDependencies)
                .map(Set::stream)
                .map(stream -> stream.anyMatch(executingSet::contains))
                .orElse(false)) {
            throw new CycleDependencyException(autoComposable);
        }
        // 记录当前组件至执行中缓存Set，用于循环依赖检测
        executingSet.add(autoComposable);

        // 获取所有依赖的组件执行结果为入参，执行当前组件
        Optional<Stream<? extends T>> dependenciesOptional = Optional.ofNullable(autoComposableDependencies)
                .map(Set::stream)
                .map(stream -> stream.map(dependency -> execute(dependency, executedMap, executingSet)));
        T result = autoComposable.executeTemplate(dependenciesOptional, ResultHolder.getCacheObject());

        // 当前组件执行完毕，清除执行中缓存Set记录
        executingSet.remove(autoComposable);

        // 缓存当前组件的执行结果后返回
        executedMap.put(autoComposable, result);
        return result;
    }
}
