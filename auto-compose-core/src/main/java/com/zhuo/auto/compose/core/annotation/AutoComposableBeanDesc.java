package com.zhuo.auto.compose.core.annotation;

import java.lang.annotation.*;

/**
 * {@code @AutoComposableBeanDesc}用于描述组件的业务逻辑，描述内容会用于`可视化的业务流程图`展示，不影响程序的执行。
 *
 * @author wangzhuo
 * @date 2023/5/8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoComposableBeanDesc {

    String value() default "";
}
