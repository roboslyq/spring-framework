/**
 * Copyright (C), 2015-2021
 * FileName: DefaultListableBeanFactoryDemo
 * Author:   roboslyq
 * Date:     2021/11/13 0:38
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/13 0:38      1.0.0               创建
 */
package com.roboslyq.learn.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.lang.Nullable;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/13
 * @since 1.0.0
 */
public class BeanLifecycleDemo {
	public static void main(String[] args) {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.addBeanPostProcessor(new MyInstantiationAwareBeanPostProcessor());
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(User.class);
		MutablePropertyValues mutableProperties = new MutablePropertyValues();
		mutableProperties.addPropertyValue("name","robos");
		beanDefinition.setPropertyValues(mutableProperties);
		beanDefinition.setInitMethodName("init");
		factory.registerBeanDefinition("user",beanDefinition);

		User user = factory.getBean("user",User.class);
		System.out.println(user);
	}

	static class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

		/**
		 * 第一次修改机会：如果有第一次机会，则后面的流程不会进行。因为已经完成了Bean的实例化
		 */
		@Override
		public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
			User user  = 	new User();
			user.setName("1");
			P.print(user);
//			return user;
			return null;//测试后面的生命周期，返回null
		}
		/**
		 * 第二次修改机会
		 */
		public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
			if(bean.getClass().equals(User.class)){
				User user = (User)bean;
				user.setName("2");
				P.print(user);
			}
			return true;
		}

		/**
		 * 第三次修改机会
		 */
		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName){
			if(bean.getClass().equals(User.class)){
				User user = (User)bean;
				user.setName("3");
				P.print(user);
			}
			return bean;
		}

		/**
		 * 第N次修改机会（此方法调用比较靠后，在相关的接口及Awared调用之后）
		 */
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName){
			if(bean.getClass().equals(User.class)){
				User user = (User)bean;
				user.setName("6");
				P.print(user);
			}
			return bean;
		}
	}




}


class P {
	static public void print(User user){
		System.out.println("当前User信息： "+ user
		);
	}
}