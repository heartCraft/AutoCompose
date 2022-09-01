package com.zhuo.auto.compose.core.dependency;


import com.zhuo.auto.compose.core.annotation.AutoComposableBeanDesc;
import com.zhuo.auto.compose.core.autocomposable.AutoComposable;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用于保存、查询组件间依赖关系，同时提供了生成依赖关系字符串、生成依赖关系图片的方法
 *
 * @Author wangzhuo
 * @Date: 2022/8/28 21:55
 */
public class AutoComposableDependenciesCache<T, R> {
    // 饿汉式单例
    public static final AutoComposableDependenciesCache INSTANCE = new AutoComposableDependenciesCache();

    private AutoComposableDependenciesCache() {
    }


    // 缓存依赖关系的map<组件的class对象，其依赖的其他组件集合>
    private Map<Class<? extends AutoComposable<T, R>>, Set<? extends AutoComposable<T, R>>> cacheMap;

    /**
     * 初始化缓存map
     *
     * @param size 组件数量
     */
    void initCacheMap(int size) {
        cacheMap = new HashMap<>((int) (size / 0.75 + 1));
    }

    /**
     * 缓存依赖关系
     *
     * @param c              指定组件的Class对象
     * @param dependentBeans 依赖的其他组件的集合
     */
    void cacheDependentBeans(Class<? extends AutoComposable<T, R>> c, Set<? extends AutoComposable<T, R>> dependentBeans) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(dependentBeans);
        Objects.requireNonNull(cacheMap);
        cacheMap.put(c, dependentBeans);
    }

    /**
     * 获取指定组件的依赖
     *
     * @param c 指定组件的Class对象
     * @return 依赖的其他组件的集合
     */
    public Set<? extends AutoComposable<T, R>> getDependentBeans(Class<? extends AutoComposable<T, R>> c) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(cacheMap);
        return cacheMap.get(c);
    }


    /**
     * 获取所有的依赖关系
     *
     * @return 所有组件的依赖关系Map<组件的class对象 ， 其依赖的其他组件集合>
     */
    public Map<Class<? extends AutoComposable<T, R>>, Set<? extends AutoComposable<T, R>>> getDependentBeans() {
        return cacheMap;
    }

    /**
     * 生成依赖关系字符串
     *
     * @return 依赖关系字符串
     */
    public String generateDependenciesString() {
        Objects.requireNonNull(cacheMap);

        // 生成依赖关系字符串
        StringBuilder sb = new StringBuilder();
        sb.append("--------AutoComposable implementation classes‘s dependencies--------\n");
        cacheMap.forEach((key, value) -> sb.append(String.format("%s -> %s\n",
                this.getAutoComposableDesc(key, "%s(%s)"),
                value.stream()
                        .map(AopUtils::getTargetClass)
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", ", "[", "]")))));
        String dependenciesString = sb.toString();

        // 控制台输出
        System.out.println("\n" + dependenciesString);

        return dependenciesString;
    }

    /**
     * 生成依赖关系图片
     *
     * @return 依赖关系图片的路径
     */
    public String generateDependenciesImg() {
        Objects.requireNonNull(cacheMap);
        // JGraphT生成图
        DefaultDirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph(DefaultEdge.class);

        Map<Class<? extends AutoComposable>, String> vertexDescMap = cacheMap.keySet().stream()
                .collect(Collectors.toMap(Function.identity(),
                        item -> getAutoComposableDesc(item, "%s\n%s")));
        // 添加顶点
        cacheMap.keySet().stream()
                .map(vertexDescMap::get)
                .forEach(directedGraph::addVertex);
        // 添加边
        cacheMap.forEach((key, value) -> value.forEach(item ->
                directedGraph.addEdge(vertexDescMap.get(AopUtils.getTargetClass(item)), vertexDescMap.get(key))));
        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<>(directedGraph);
        graphAdapter.getEdgeToCellMap().forEach((defaultEdge, cell) -> cell.setValue(null));
        // 层次结构布局
        new mxHierarchicalLayout(graphAdapter, SwingConstants.NORTH).execute(graphAdapter.getDefaultParent());
        try {
            URL rootUrl = Objects.requireNonNull(getClass().getResource("/"));
            File imgFile = new File(rootUrl.getFile() + "AutoComposableDependencies.png");
            imgFile.createNewFile();
            ImageIO.write(mxCellRenderer.createBufferedImage(graphAdapter, null, 4, Color.WHITE, true, null),
                    "PNG",
                    imgFile);

            // 控制台输出依赖关系图路径
            System.out.println("\nAutoComposableDependencies.png generate successfully: " + imgFile.getPath());

            return imgFile.getPath();
        } catch (Exception e) {
            System.out.println("Fail to generate AutoComposableDependencies.png");
        }
        return "";
    }

    /**
     * 获取组件的描述
     *
     * @param c      组件的Class对象
     * @param format 组件的描述格式
     * @return 组件的描述
     */
    private String getAutoComposableDesc(Class<? extends AutoComposable> c, String format) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(c, AutoComposableBeanDesc.class))
                .map(AutoComposableBeanDesc::value)
                .filter(StringUtils::hasText)
                .map(desc -> String.format(format, c.getSimpleName(), desc))
                .orElse(c.getSimpleName());
    }
}
