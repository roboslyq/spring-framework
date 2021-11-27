package com.roboslyq.learn.aop;

import org.springframework.stereotype.Service;

@Service
public class UserDaoNoInterface {
	public void addUser() {
		System.out.println("add user ");
	}

	public void deleteUser() {
		System.out.println("delete user ");
	}
}
