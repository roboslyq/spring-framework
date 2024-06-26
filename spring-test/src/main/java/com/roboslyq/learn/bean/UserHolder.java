/**
 * Copyright (C), 2015-2021
 * FileName: UserHolder
 * Author:   roboslyq
 * Date:     2021/11/17 23:54
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/17 23:54      1.0.0               创建
 */
package com.roboslyq.learn.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/17
 * @since 1.0.0
 */
public class UserHolder implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware,
		InitializingBean, SmartInitializingSingleton, DisposableBean {

	private final User user;

	private Integer number;

	private String description;

	private ClassLoader classLoader;

	private BeanFactory beanFactory;

	private String beanName;

	private Environment environment;

	public UserHolder(User user) {
		this.user = user;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 依赖于注解驱动
	 * 当前场景：BeanFactory
	 */
	@PostConstruct
	public void initPostConstruct() {
		// postProcessBeforeInitialization V3 -> initPostConstruct V4
		this.description = "The user holder V4";
		System.out.println("initPostConstruct() = " + description);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// initPostConstruct V4 -> afterPropertiesSet V5
		this.description = "The user holder V5";
		System.out.println("afterPropertiesSet() = " + description);
	}

	/**
	 * 自定义初始化方法
	 */
	public void init() {
		// initPostConstruct V5 -> afterPropertiesSet V6
		this.description = "The user holder V6";
		System.out.println("init() = " + description);
	}

	@PreDestroy
	public void preDestroy() {
		// postProcessBeforeDestruction : The user holder V9
		this.description = "The user holder V10";
		System.out.println("preDestroy() = " + description);
	}

	@Override
	public void destroy() throws Exception {
		// preDestroy : The user holder V10
		this.description = "The user holder V11";
		System.out.println("destroy() = " + description);
	}

	public void doDestroy() {
		// destroy : The user holder V11
		this.description = "The user holder V12";
		System.out.println("doDestroy() = " + description);
	}

	@Override
	public String toString() {
		return "UserHolder{" +
				"user=" + user +
				", number=" + number +
				", description='" + description + '\'' +
				", beanName='" + beanName + '\'' +
				'}';
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void afterSingletonsInstantiated() {
		// postProcessAfterInitialization V7 -> afterSingletonsInstantiated V8
		this.description = "The user holder V8";
		System.out.println("afterSingletonsInstantiated() = " + description);
	}

	protected void finalize() throws Throwable {
		System.out.println("The UserHolder is finalized...");
	}
}