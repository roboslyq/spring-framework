/**
 * Copyright (C), 2015-2021
 * FileName: MessageDemo
 * Author:   roboslyq
 * Date:     2021/12/13 22:21
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/12/13 22:21      1.0.0               创建
 */
package com.roboslyq.learn.message;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Locale;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/12/13
 * @since 1.0.0
 */
public class MessageDemo {
	public static void main(String[] args) {
		MessageSource resources = new ClassPathXmlApplicationContext("classpath*:/META-INF/message/message-i18n.xml");


		String message = resources.getMessage("message", null, "Default", Locale.ENGLISH);
		System.out.println(message);

		String message2 = resources.getMessage("message", null, "Default", Locale.CHINA);
		System.out.println(message2);

		String message3 = resources.getMessage("argument.required",
				new Object [] {"userDao"}, "Required", Locale.ENGLISH);
		System.out.println(message3);
	}

}
