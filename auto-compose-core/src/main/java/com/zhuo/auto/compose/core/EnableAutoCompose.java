package com.zhuo.auto.compose.core;

import com.zhuo.auto.compose.core.dependency.AutoComposableDependenciesCacheInitializeListener;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用AutoCompose注解，供非SpringBoot的Spring项目使用
 *
 * @author wangzhuo
 * @date 2024/1/4 19:22
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AutoComposeConfiguration.class, AutoComposableDependenciesCacheInitializeListener.class})
public @interface EnableAutoCompose {
}
