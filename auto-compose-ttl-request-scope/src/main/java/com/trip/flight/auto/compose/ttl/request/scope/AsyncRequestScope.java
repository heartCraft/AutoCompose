package com.trip.flight.auto.compose.ttl.request.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * @Author wangzhuo
 * @Date: 2022/8/28 14:54
 */
public class AsyncRequestScope implements Scope {

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return TtlBeanCache.getBean(name, objectFactory);
    }

    @Override
    public Object remove(String name) {
        throw new UnsupportedOperationException("Can't remove from AsyncRequestScope");
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
