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

import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/6
 * @since 1.0.0
 */


public class User implements InitializingBean {
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

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", password='" + password + '\'' +
				", birthday=" + birthday +
				'}';
	}

	@PostConstruct
	public void post(){
		this.setName("6");
		P.print(this);
	}


	public void init(){
		this.setName("5");
		P.print(this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setName("4");
		P.print(this);
	}
}
