package com.roboslyq.learn.bean.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 每三次重新生成一个新的Bean
 * @Author roboslyq
 * @desc 自定义Scope
 * @since 2021/12/12 19:35
 */
public class CustomScope implements Scope {
	private AtomicInteger count;
	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Object obj = objectFactory.getObject();
		return null;
	}

	@Override
	public Object remove(String name) {
		return null;
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
