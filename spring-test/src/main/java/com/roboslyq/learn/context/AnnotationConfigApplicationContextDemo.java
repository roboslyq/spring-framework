/**
 * Copyright (C), 2015-2021
 * FileName: AnnotationConfigApplicationContextDemo
 * Author:   roboslyq
 * Date:     2021/12/13 21:58
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/12/13 21:58      1.0.0               创建
 */
package com.roboslyq.learn.context;

import com.roboslyq.learn.bean.User;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/12/13
 * @since 1.0.0
 */
@Configuration
public class AnnotationConfigApplicationContextDemo {

	@Test
	public void test1() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.roboslyq.learn.context");
		context.refresh();
		User user = context.getBean(User.class);
		System.out.println(user.getName());
	}

	@Test
	public void test2() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfigApplicationContextDemo.class);
		User user = context.getBean(User.class);
		System.out.println(user.getName());
	}

	@Bean
	public User init(){
		User user = new User();
		user.setName("11");
		return user;
	}
}
