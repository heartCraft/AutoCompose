package com.trip.flight.auto.compose.core.dependency;

import com.trip.flight.auto.compose.core.AutoComposable;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author wangzhuo
 * @Date: 2022/8/29 17:19
 */
public class AutoComposableDependenciesCacheInitializeListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 所有实现AutoComposable接口的beanName与bean的map
        Map<String, AutoComposable> autoComposableBeanMap = event.getApplicationContext().getBeansOfType(AutoComposable.class);
        if (autoComposableBeanMap == null || autoComposableBeanMap.size() == 0) {
            return;
        }

        // 初始化缓存map
        AutoComposableDependenciesCache.INSTANCE.initCacheMap(autoComposableBeanMap.size() / 2);

        // 缓存每个实现了AutoComposable接口的类的依赖关系
        autoComposableBeanMap.entrySet().stream()
                .filter(entry -> !AopUtils.isAopProxy(entry.getValue()))
                .forEach(entry -> {
                    // 用于记录已扫描的BeanName
                    Set<String> scannedBeanNames = new HashSet<>();
                    // 用于保存依赖的实现了AutoComposable接口的bean
                    List<AutoComposable> autoComposableDependencies = new ArrayList<>();
                    // 扫描获取依赖关系
                    scanDependencies(entry.getKey(), event.getApplicationContext(), autoComposableBeanMap, scannedBeanNames, autoComposableDependencies);
                    // 缓存
                    AutoComposableDependenciesCache.INSTANCE.cacheDependentBeans(entry.getValue().getClass(), autoComposableDependencies);
                });
    }

    /**
     * 扫描依赖的实现AutoComposable接口的bean
     *
     * @param beanName                   指定扫描的beanName
     * @param applicationContext         Spring application context
     * @param autoComposableBeanMap      所有实现AutoComposable接口的beanName与bean的map
     * @param scannedBeanNames           已扫描的BeanName
     * @param autoComposableDependencies 依赖的实现AutoComposable接口的bean
     */
    private void scanDependencies(String beanName,
                                  ConfigurableApplicationContext applicationContext,
                                  Map<String, AutoComposable> autoComposableBeanMap,
                                  Set<String> scannedBeanNames,
                                  List<AutoComposable> autoComposableDependencies) {
        // 判断当前beanName是否已扫描过
        if (scannedBeanNames.contains(beanName)) {
            return;
        }
        // 记录已扫描的beanName，避免循环依赖导致递归死循环
        scannedBeanNames.add(beanName);

        // 遍历其依赖的beanNames
        for (String dependencyBeanName : getDependenciesForBean(beanName, applicationContext)) {

            // 当其依赖的bean实现了AutoComposable接口，记录
            if (autoComposableBeanMap.containsKey(dependencyBeanName)) {
                autoComposableDependencies.add(autoComposableBeanMap.get(dependencyBeanName));
                continue;
            }

            // 递归扫描
            scanDependencies(dependencyBeanName, applicationContext, autoComposableBeanMap, scannedBeanNames, autoComposableDependencies);
        }
    }

    /**
     * 获取指定beanName依赖的其他beanNames
     *
     * @param beanName
     * @param applicationContext
     * @return
     */
    private String[] getDependenciesForBean(String beanName,
                                            ConfigurableApplicationContext applicationContext) {
        // 判断传入的beanName是否有scopedTarget的bean，如有则使用scopedTarget的beanName
        String scopedTargetBeanName = ScopedProxyUtils.getTargetBeanName(beanName);
        if (applicationContext.containsBean(scopedTargetBeanName)) {
            beanName = scopedTargetBeanName;
        }

        // 获取其依赖的beanNames
        applicationContext.getBean(beanName);
        return applicationContext.getBeanFactory().getDependenciesForBean(beanName);
    }
}
