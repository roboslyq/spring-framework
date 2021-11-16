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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/13
 * @since 1.0.0
 */
public class DefaultListableBeanFactoryDemo {
	public static void main(String[] args) {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(User.class);
		MutablePropertyValues mutableProperties = new MutablePropertyValues();
		mutableProperties.addPropertyValue("name","robos");
		beanDefinition.setPropertyValues(mutableProperties);
		factory.registerBeanDefinition("user",beanDefinition);

		User user = factory.getBean("user",User.class);
		System.out.println(user);
	}

}
