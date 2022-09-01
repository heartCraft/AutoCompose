package com.zhuo.auto.compose.core.exception;

import org.springframework.aop.support.AopUtils;

/**
 * 组件循环依赖异常
 *
 * @Author wangzhuo
 * @Date: 2020/9/15 18:09
 */
public class CycleDependencyException extends RuntimeException {

    public CycleDependencyException() {
        super();
    }

    public CycleDependencyException(String message) {
        super(message);
    }

    public CycleDependencyException(Object object) {
        super("Class: \"" + AopUtils.getTargetClass(object) + "\" has Cycle AutoComposable Dependency");
    }
}
