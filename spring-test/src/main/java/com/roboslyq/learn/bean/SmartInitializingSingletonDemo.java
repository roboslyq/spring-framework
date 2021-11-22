/**
 * Copyright (C), 2015-2021
 * FileName: SmartInitializingSingletonDemo
 * Author:   roboslyq
 * Date:     2021/11/21 20:33
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/21 20:33      1.0.0               创建
 */
package com.roboslyq.learn.bean;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/21
 * @since 1.0.0
 */
public class SmartInitializingSingletonDemo {
	public static void main(String[] args) throws IOException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.setAllowCircularReferences(true);
		context.register(SmartInitializingSingletonDemo.class);
		context.refresh();
		context.start();
		context.stop();
		System.in.read();
		context.close();

	}

	@Bean
	public Sdemo init(){
		return  new Sdemo();
	}



}


class Sdemo implements SmartInitializingSingleton, LifecycleProcessor {
	boolean isRunning =false;

	@Override
	public void start() {
		isRunning = true;
		System.out.println("start");

	}

	@Override
	public void stop() {
		isRunning = false;
		System.out.println("stop");

	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void afterSingletonsInstantiated() {
		System.out.println("afterSingletonsInstantiated");
	}

	@Override
	public void onRefresh() {
		System.out.println("onRefresh");

	}

	@Override
	public void onClose() {
		System.out.println("onClose");

	}
}