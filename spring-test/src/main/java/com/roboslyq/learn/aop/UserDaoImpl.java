/**
 * Copyright (C), 2015-2021
 * FileName: UserDaoImpl
 * Author:   roboslyq
 * Date:     2021/11/23 22:49
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/23 22:49      1.0.0               创建
 */
package com.roboslyq.learn.aop;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/23
 * @since 1.0.0
 */
public class UserDaoImpl implements UserDao{

	public void addUser() {
		System.out.println("add user ");
	}

	public void deleteUser() {
		System.out.println("delete user ");
	}

}