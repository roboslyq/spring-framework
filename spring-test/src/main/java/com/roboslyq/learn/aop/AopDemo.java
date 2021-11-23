/**
 * Copyright (C), 2015-2021
 * FileName: AopDemo
 * Author:   roboslyq
 * Date:     2021/11/8 23:15
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/8 23:15      1.0.0               创建
 */
package com.roboslyq.learn.aop;

import com.roboslyq.learn.bean.User;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *
 * 〈切面编程〉
 * @author roboslyq
 * @date 2021/11/8
 * @since 1.0.0
 */
@EnableAspectJAutoProxy
public class AopDemo {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopDemo.class);
		UserDao userDao = context.getBean(UserDao.class);
		userDao.addUser();
	}

	@Bean
	public AspectJAwareAdvisorAutoProxyCreator initAspect(){
		return new AspectJAwareAdvisorAutoProxyCreator();
	}

	@Bean
	public UserDao userDao(){
		return new UserDaoImpl();
	}
	@Bean
	public Logger logger(){
		return new Logger();
	}

}
