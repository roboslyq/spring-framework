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
public class AopDemoMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopDemoMain.class);
		UserDao userDao = context.getBean(UserDao.class);
		userDao.addUser();

		UserDaoNoInterface userDaoNoInterface = context.getBean(UserDaoNoInterface.class);
		userDaoNoInterface.addUser();
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
	public UserDaoNoInterface userDaoNoInterface(){
		return new UserDaoNoInterface();
	}
	@Bean
	public Logger logger(){
		return new Logger();
	}

}
