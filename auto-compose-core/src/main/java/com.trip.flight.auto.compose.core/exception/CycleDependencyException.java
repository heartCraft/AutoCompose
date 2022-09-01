package com.trip.flight.auto.compose.core.exception;

/**
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
        super("Class: \"" + object.getClass().getSimpleName().split("\\$")[0] + "\" has Cycle AutoComposable Dependency");
    }
}
