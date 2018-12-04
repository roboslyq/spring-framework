/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * Handler execution chain, consisting of handler object and any handler interceptors.
 * Returned by HandlerMapping's {@link HandlerMapping#getHandler} method.
 *
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see HandlerInterceptor
 * HandlerExecutionChain执行链，包含handler对象及所有Handler拦截器
 *  在Java SE 1.5之前，没有泛型的情况的下，通过对类型Object的引用来实现参数的“任意化”，
 *  “任意化”带来的缺点是要做显式的强制类型转换，而这种转换是要求开发者对实际参数类型可以预知的情况下进行的。
 * 因J2SE 5.0 (1.5.0) Tiger 老虎是在2004年10月份发布，而此类2003年六月份就存在了，所以没有使用泛型而是Object类型
 */
public class HandlerExecutionChain {

	private static final Log logger = LogFactory.getLog(HandlerExecutionChain.class);
	/**
	 * 对应的Handler
	 */
	private final Object handler;

	@Nullable
	/**
	 * 对应的拦截器数组
	 */
	private HandlerInterceptor[] interceptors;

	@Nullable
	/**
	 * 对应的拦截器集合
	 */
	private List<HandlerInterceptor> interceptorList;
	/**
	 * 当前拦截器索引，从-1开始计数
	 */
	private int interceptorIndex = -1;


	/**
	 * Create a new HandlerExecutionChain.
	 * @param handler the handler object to execute
	 *  构造函数，指定一个处理器。拦截器默认为空
	 */
	public HandlerExecutionChain(Object handler) {
		this(handler, (HandlerInterceptor[]) null);
	}

	/**
	 * Create a new HandlerExecutionChain.
	 * @param handler the handler object to execute
	 * @param interceptors the array of interceptors to apply
	 * (in the given order) before the handler itself executes
	 *  构造函数，指定一个处理器和一系列拦截器
	 */
	public HandlerExecutionChain(Object handler, @Nullable HandlerInterceptor... interceptors) {
		//如果是HandlerExecutionChain，合并originalChain到当前HandlerExecutionChain中
		if (handler instanceof HandlerExecutionChain) {
			HandlerExecutionChain originalChain = (HandlerExecutionChain) handler;
			this.handler = originalChain.getHandler();
			this.interceptorList = new ArrayList<>();
			CollectionUtils.mergeArrayIntoCollection(originalChain.getInterceptors(), this.interceptorList);
			CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
		}
		else {
			//可能为String类型，即handlerBean名称
			this.handler = handler;
			this.interceptors = interceptors;
		}
	}


	/**
	 * Return the handler object to execute.
	 * 返回Hanlder
	 */
	public Object getHandler() {
		return this.handler;
	}

	/**
	 * 添加拦截器
	 * @param interceptor
	 */
	public void addInterceptor(HandlerInterceptor interceptor) {
		initInterceptorList().add(interceptor);
	}

	public void addInterceptors(HandlerInterceptor... interceptors) {
		if (!ObjectUtils.isEmpty(interceptors)) {
			CollectionUtils.mergeArrayIntoCollection(interceptors, initInterceptorList());
		}
	}

	private List<HandlerInterceptor> initInterceptorList() {
		if (this.interceptorList == null) {
			this.interceptorList = new ArrayList<>();
			if (this.interceptors != null) {
				// An interceptor array specified through the constructor
				CollectionUtils.mergeArrayIntoCollection(this.interceptors, this.interceptorList);
			}
		}
		this.interceptors = null;
		return this.interceptorList;
	}

	/**
	 * Return the array of interceptors to apply (in the given order).
	 * @return the array of HandlerInterceptors instances (may be {@code null})
	 */
	@Nullable
	public HandlerInterceptor[] getInterceptors() {
		if (this.interceptors == null && this.interceptorList != null) {
			this.interceptors = this.interceptorList.toArray(new HandlerInterceptor[0]);
		}
		return this.interceptors;
	}


	/**
	 * Apply preHandle methods of registered interceptors.
	 * @return {@code true} if the execution chain should proceed with the
	 * next interceptor or the handler itself. Else, DispatcherServlet assumes
	 * that this interceptor has already dealt with the response itself.
	 */
	boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = 0; i < interceptors.length; i++) {
				HandlerInterceptor interceptor = interceptors[i];
				if (!interceptor.preHandle(request, response, this.handler)) {
					triggerAfterCompletion(request, response, null);
					return false;
				}
				this.interceptorIndex = i;
			}
		}
		return true;
	}

	/**
	 * Apply postHandle methods of registered interceptors.
	 */
	void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv)
			throws Exception {

		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = interceptors.length - 1; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
				interceptor.postHandle(request, response, this.handler, mv);
			}
		}
	}

	/**
	 * Trigger afterCompletion callbacks on the mapped HandlerInterceptors.
	 * Will just invoke afterCompletion for all interceptors whose preHandle invocation
	 * has successfully completed and returned true.
	 */
	void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex)
			throws Exception {

		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = this.interceptorIndex; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
				try {
					interceptor.afterCompletion(request, response, this.handler, ex);
				}
				catch (Throwable ex2) {
					logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
				}
			}
		}
	}

	/**
	 * Apply afterConcurrentHandlerStarted callback on mapped AsyncHandlerInterceptors.
	 */
	void applyAfterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response) {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = interceptors.length - 1; i >= 0; i--) {
				if (interceptors[i] instanceof AsyncHandlerInterceptor) {
					try {
						AsyncHandlerInterceptor asyncInterceptor = (AsyncHandlerInterceptor) interceptors[i];
						asyncInterceptor.afterConcurrentHandlingStarted(request, response, this.handler);
					}
					catch (Throwable ex) {
						logger.error("Interceptor [" + interceptors[i] + "] failed in afterConcurrentHandlingStarted", ex);
					}
				}
			}
		}
	}


	/**
	 * Delegates to the handler and interceptors' {@code toString()}.
	 */
	@Override
	public String toString() {
		Object handler = getHandler();
		StringBuilder sb = new StringBuilder();
		sb.append("HandlerExecutionChain with [").append(handler).append("] and ");
		if (this.interceptorList != null) {
			sb.append(this.interceptorList.size());
		}
		else if (this.interceptors != null) {
			sb.append(this.interceptors.length);
		}
		else {
			sb.append(0);
		}
		return sb.append(" interceptors").toString();
	}

}
