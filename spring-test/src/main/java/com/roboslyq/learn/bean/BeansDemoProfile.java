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
 * 〈激活指定的 profile
 * 1 在配置文件中指定
 * 		spring.profiles.active=dev
 * 2 命令指定
 * 		java -jar [jar-name] --spring.profiles.active=dev
 * 3 虚拟机参数
 * 		-Dspring.profiles.active=dev
 * 4 通过System.setProperty("spring.profiles.active","dev")设置环境变量
 *
 * @author roboslyq
 * @date 2021/11/6
 * @since 1.0.0
 */
public class BeansDemoProfile {
	public static void main(String[] args) {
		System.setProperty("spring.profiles.active","dev");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/beans-profile.xml");
		context.getEnvironment().setActiveProfiles("dev");
		User user = context.getBean("user",User.class);
		System.out.println(user.getClass());
		System.out.println(user);
	}

}
