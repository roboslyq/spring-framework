/**
 * Copyright (C), 2015-2021
 * FileName: AnnotationApplicationContextDemo
 * Author:   roboslyq
 * Date:     2021/11/8 23:15
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/8 23:15      1.0.0               创建
 */
package com.roboslyq.learn.bean;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/8
 * @since 1.0.0
 */
public class AnnotationApplicationContextDemo {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AnnotationApplicationContextDemo.class);
		User user = context.getBean("user",User.class);
		System.out.println(user.getName());
	}

	@Bean
	public User user(){
		User user = new User();
		user.setName("roboslyq");
		return user;
	}

}
