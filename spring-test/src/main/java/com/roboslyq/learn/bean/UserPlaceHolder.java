/**
 * Copyright (C), 2015-2021
 * FileName: User
 * Author:   roboslyq
 * Date:     2021/11/6 23:45
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/6 23:45      1.0.0               创建
 */
package com.roboslyq.learn.bean;

import java.util.Date;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/6
 * @since 1.0.0
 */
public class UserPlaceHolder {
	private String name;
	private String password;

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	private Date birthday;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


}
