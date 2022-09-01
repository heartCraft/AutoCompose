package com.zhuo.auto.compose.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author wangzhuo
 * @date 2024/1/4 17:01
 */
@Configuration
public class AutoComposeConfiguration {

    @Bean
    public AutoComposeUtils autoComposeUtils() {
        return new AutoComposeUtils();
    }
}
