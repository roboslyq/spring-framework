package com.roboslyq.learn.utils;

import com.roboslyq.learn.bean.User;

import java.util.Date;

/**
 * @author roboslyq
 * @desc: TODO
 * @since 2021/11/26 23:03
 */
public class BeanUtilsDemo {

	public static void main(String[] args) {
		User user = new User();
		user.setName("r");
		user.setPassword("123");
		user.setBirthday(new Date());

		User user2 = new User();
		user2.setPassword("456");

		BeanUtils.copyPropertiesIgnoreNull(user2,user);

		System.out.println(user);
	}


}
