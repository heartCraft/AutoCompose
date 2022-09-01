package com.zhuo.auto.compose.core.dependency;

import com.zhuo.auto.compose.core.autocomposable.AutoComposable;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 初始化并缓存所有组件的依赖关系监听器
 *
 * @Author wangzhuo
 * @Date: 2022/8/29 17:19
 */
public class AutoComposableDependenciesCacheInitializeListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 初始化并缓存所有组件的依赖关系
     *
     * @param event ApplicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 获取所有组件Map<BeanName,Bean>。组件 == 实现了AutoComposable接口的Bean对象
        Map<String, AutoComposable> autoComposableBeanMap =
                event.getApplicationContext().getBeansOfType(AutoComposable.class);
        if (autoComposableBeanMap == null || autoComposableBeanMap.isEmpty()) {
            return;
        }

        // 过滤掉因指定Scope而生成的代理Bean
        autoComposableBeanMap = autoComposableBeanMap.entrySet().stream()
                .filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 初始化缓存map
        AutoComposableDependenciesCache.INSTANCE.initCacheMap(autoComposableBeanMap.size());

        // 获取组件间依赖关系的工具类
        DependentAutoComposableBeanUtil dependentAutoComposableBeanUtil =
                new DependentAutoComposableBeanUtil(event.getApplicationContext(), autoComposableBeanMap);

        // 遍历所有组件，获取并缓存依赖关系
        autoComposableBeanMap.forEach((key, value) -> {
            // 获取并缓存组件的依赖关系
            AutoComposableDependenciesCache.INSTANCE.cacheDependentBeans(
                    AopUtils.getTargetClass(value),
                    dependentAutoComposableBeanUtil.getBeans(key));
        });
    }

    /**
     * 获取组件间依赖关系的工具类
     */
    private static class DependentAutoComposableBeanUtil {

        // Spring application context
        private final ConfigurableApplicationContext applicationContext;

        // 所有组件Map<BeanName,Bean>。组件就是实现了AutoComposable接口的Bean
        private final Map<String, AutoComposable> autoComposableBeanMap;

        /**
         * @param applicationContext    Spring application context
         * @param autoComposableBeanMap 所有组件Map<BeanName,Bean>
         */
        DependentAutoComposableBeanUtil(ConfigurableApplicationContext applicationContext,
                Map<String, AutoComposable> autoComposableBeanMap) {
            this.applicationContext = applicationContext;
            this.autoComposableBeanMap = autoComposableBeanMap;
        }

        /**
         * 根据组件的BeanName，获取其依赖的其他组件集合
         *
         * @param beanName 组件的BeanName
         * @return 其依赖的其他组件集合
         */
        Set<AutoComposable> getBeans(String beanName) {
            Set<AutoComposable> beans = new HashSet<>();
            // 扫描获取指定组件依赖的其他组件集合
            scan(beanName, new HashSet<>(), beans);
            return beans;
        }

        /**
         * 扫描指定BeanName依赖的组件集合
         *
         * @param beanName         指定BeanName
         * @param scannedBeanNames 已扫描过的BeanName
         * @param beans            依赖的组件集合，即实现了AutoComposable接口的Bean集合
         */
        private void scan(String beanName,
                Set<String> scannedBeanNames,
                Set<AutoComposable> beans) {
            // 判断当前BeanName是否已扫描过
            if (scannedBeanNames.contains(beanName)) {
                return;
            }
            // 记录已扫描的beanName，避免循环依赖导致递归死循环
            scannedBeanNames.add(beanName);

            // 遍历Spring依赖的BeanName集合
            for (String dependencyBeanName : getDependenciesForBean(beanName)) {

                // 当其是实现AutoComposable接口的Bean时，记录此Bean，不再递归扫描
                if (autoComposableBeanMap.containsKey(dependencyBeanName)) {
                    beans.add(autoComposableBeanMap.get(dependencyBeanName));
                    continue;
                }

                // 递归扫描
                scan(dependencyBeanName, scannedBeanNames, beans);
            }
        }

        /**
         * 获取指定BeanName的Spring依赖的BeanName集合
         *
         * @param beanName
         * @return
         */
        private String[] getDependenciesForBean(String beanName) {
            // 判断传入的BeanName是否存在因指定Scope而被代理的的Bean，如有则替换为被代理的BeanName
            if (!ScopedProxyUtils.isScopedTarget(beanName)) {
                String scopedTargetBeanName = ScopedProxyUtils.getTargetBeanName(beanName);
                if (applicationContext.containsBean(scopedTargetBeanName)) {
                    beanName = scopedTargetBeanName;
                }
            }

            // 判断传入的BeanName是否存在
            if (!applicationContext.containsBean(beanName)) {
                return new String[0];
            }

            // 获取其Spring依赖的BeanName集合
            applicationContext.getBean(beanName);
            return applicationContext.getBeanFactory().getDependenciesForBean(beanName);
        }

    }


}
