package com.trip.flight.auto.compose.ttl.request.scope;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static com.trip.flight.auto.compose.core.scope.AsyncRequestScopeConstants.SCOPE_NAME;

/**
 * @Author wangzhuo
 * @Date: 2022/8/28 14:43
 */
public class AsyncRequestScopeBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.registerScope(SCOPE_NAME, new AsyncRequestScope());
    }

}
