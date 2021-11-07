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

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/6
 * @since 1.0.0
 */
public class BeansDemo {
	public static void main(String[] args) {
		System.out.println(BeansDemo.class.getResource("/"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/beans.xml");
		User user = context.getBean("user",User.class);
		System.out.println(user.getClass());
	}

}
