/**
 * Copyright (C), 2015-2021
 * FileName: BeansDemo
 * Author:   roboslyq
 * Date:     2021/11/6 23:48
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/6 23:48      1.0.0               创建
 */
package com.roboslyq.learn.bean;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Properties;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/6
 * @since 1.0.0
 */
public class BeansPlaceHolderDemo {
	public static void main(String[] args) {
		System.out.println(BeansPlaceHolderDemo.class.getResource("/"));
//		System.setProperty("name","luoyq");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

		context.setConfigLocations("classpath:/META-INF/beans-placeholder.xml");
		System.out.println(context.getEnvironment().getProperty("name"));
		context.refresh();
		UserPlaceHolder user = context.getBean("user",UserPlaceHolder.class);
		System.out.println(user.getName());
	}

}
