/**
 * Copyright (C), 2015-2021
 * FileName: Logger
 * Author:   roboslyq
 * Date:     2021/11/23 22:50
 * Description:
 * History:
 * <author>                 <time>          <version>          <desc>
 * luo.yongqian         2021/11/23 22:50      1.0.0               创建
 */
package com.roboslyq.learn.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/23
 * @since 1.0.0
 */
@Aspect
public class Logger {
	@Pointcut("execution(* com.roboslyq.learn.aop..*.*(..))" )
	public void pointCut(){}

	@Before(value ="pointCut()")
	public void recordBefore(){
		System.out.println("recordBefore");
	}

	@After(value ="pointCut()")
	public void recordAfter(){
		System.out.println("recordAfter");
	}

}