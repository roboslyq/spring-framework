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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 * 一、此类的功能说明
 * （1）此类是AbstractApplicationContext's 委托的后置处理器代理类，包括注册postProcessor,调用postProcessor等功能。
 * 		其中postProcessor包括两在类：
 * 			BeanFactoryPostProcessor
 * 			BeanPostProcessor
 * （2）实际上BeanFactoryPostProcessor又细分为两类:
 *		1.类常规BeanFactoryPostProcessor，
 *		2.是BeanDefinitionRegistryPostProcessor。
 *		BeanDefinitionRegistryPostProcessor其实继承自BeanFactoryPostProcessor,是一种特殊的BeanFactoryPostProcessor。
 *		BeanDefinitionRegistryPostProcessor的设计目的是在常规BeanFactoryPostProcessor处理BeanFactory(也就是容器)前
 *		先对bean注册做处理，比如注册更多的bean，实现此目的是通过BeanDefinitionRegistryPostProcessor定义的方法
 *		postProcessBeanDefinitionRegistry。如果一个实现类是BeanDefinitionRegistryPostProcessor,那么
 *		它的postProcessBeanDefinitionRegistry方法总是要早与它的postProcessBeanFactory方法被调用。
 *	二、背景介绍 :
 *	（1） ApplicationContext对象在构造函数执行时会创建一些BeanFactoryPostProcessor,比如
 *		  AnnotationConfigEmbeddedWebApplicationContext构造函数中最终会通过AnnotationConfigUtils注册
 *		  进来一些BeanFactoryPostProcessor/BeanPostProcessor
 *   （2）ApplicationContext的ApplicationContextInitializer被执行时会创建特定功能的，BeanFactoryPostProcessor记录
 *		在ApplicationContext中(注意：这里不是注册到容器中，而是记录为ApplicationContext的属性);
 *	（3）ApplicationContext.refresh()中,BeanFactory prepare 和 post process 之后，会调用 invokeBeanFactoryPostProcessors()
 *		委托执行这些BeanFactoryPostProcessor完成指定的功能
 *	举例来看，一个缺省的Web SpringBootApplication 会被添加这几个 BeanFactoryPostProcessor
 *      ConfigurationWarningsApplicationContextInitializer$ConfigurationWarningsPostProcessor
 *	    SharedMetadataReaderFactoryContextInitializer$CachingMetadataReaderFactoryPostProcessor
 *	    ConfigFileApplicationListener$PropertySourceOrderingPostProcessor
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {
	//构造函数私有，不可以直接创建，因为此代理提供的是静态方法
	private PostProcessorRegistrationDelegate() {
	}

	/**
	 * 调用BeanFactoryPostProcessor
	 * @参数 beanFactory 应用上下文的 BeanFactory 实例
	 * @参数 beanFactoryPostProcessors 应用上下文指定要执行的 BeanFactoryPostProcessor
	 **/
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		//定义已经处理过的processor,防止重复处理
		Set<String> processedBeans = new HashSet<>();

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		// 因为BeanDefinitionRegistryPostProcessors优先于 BeanFactoryPostProcessors执行，所以这里优先判断
		// 是否是BeanDefinitionRegistry
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// 用于记录常规 BeanFactoryPostProcessor
			List<BeanFactoryPostProcessor> regularPostProcessors = new LinkedList<>();
			/**
			 * 用于记录 BeanDefinitionRegistryPostProcessor
			 * 来源有两个：
			 * 		（1）方法参数：beanFactoryPostProcessors
			 * 		（2）以Bean形式定义的
			 */
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new LinkedList<>();
			/**
			 * 遍历所有参数传递进来的 BeanFactoryPostProcessor(它们并没有作为bean注册在容器中)
			 * 将所有参数传入的 BeanFactoryPostProcessor 分成两组 :
			 * 			BeanDefinitionRegistryPostProcessor 和常规 BeanFactoryPostProcessor
			 * 			1.如果是BeanDefinitionRegistryPostProcessor，现在执行postProcessBeanDefinitionRegistry()，
			 * 			2.否则记录为一个常规 BeanFactoryPostProcessor，现在不执行处理
			 */
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					//如果是BeanDefinitionRegistryPostProcessor，优先执行postProcessBeanDefinitionRegistry
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					//添加到Processors列表中
					registryProcessors.add(registryProcessor);
				}
				else {
					//添加到Processors列表中
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			// currentRegistryProcessors 用于记录当前正要被执行的BeanDefinitionRegistryPostProcessor
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			// 1. 对 Bean形式 BeanDefinitionRegistryPostProcessor + PriorityOrdered （优先顺序）的调用
			// 找出所有容器中注册为bean存在的BeanDefinitionRegistryPostProcessor
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			//排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// Bean形式存在的 BeanDefinitionRegistryPostProcessor 也添加到 registryProcessors 中
			registryProcessors.addAll(currentRegistryProcessors);
			// 对bean形式存在的 BeanDefinitionRegistryPostProcessor 执行其对
			// BeanDefinitionRegistry的postProcessBeanDefinitionRegistry()
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			// 2. 对 Bean形式 BeanDefinitionRegistryPostProcessor + Ordered（普通顺序）的调用
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// Bean形式存在的 BeanDefinitionRegistryPostProcessor 也添加到 registryProcessors 中
			registryProcessors.addAll(currentRegistryProcessors);
			// 对Bean形式存在的 BeanDefinitionRegistryPostProcessor 执行其对
			// BeanDefinitionRegistry的postProcessBeanDefinitionRegistry()
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			// 3. 对 Bean形式 BeanDefinitionRegistryPostProcessor , 并且未实现PriorityOrdered或者Ordered接口进行处理，
			// 直到没有未被处理的
			boolean reiterate = true; //reiterate = re + iterate,反复的遍列
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			//处理完上面流程后，开始调用上面添加的所有processor的postProcessBeanFactory回调方法
			// 因为BeanDefinitionRegistryPostProcessor继承自BeanFactoryPostProcessor,所以这里
			// 也对所有 BeanDefinitionRegistryPostProcessor 调用其方法 postProcessBeanFactory()
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			// 对所有常规 BeanFactoryPostProcessor 调用其方法 postProcessBeanFactory()
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}
		//如果不是BeanDefinitionRegistry类型的Factory，直接调用处理
		else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// 以上逻辑执行了所有参数传入的和以bean定义方式存在的BeanDefinitionRegistryPostProcessor,
		// 也执行了所有参数传入的BeanFactoryPostProcessor, 但是尚未处理所有以bean定义方式存在的
		// BeanFactoryPostProcessor, 下面的逻辑处理这部分 BeanFactoryPostProcessor.

		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 将所有目前记录的所有BeanFactoryPostProcessor分成三部分 :
		// 1. 实现了 PriorityOrdered 接口的,
		// 2. 实现了 Ordered 接口的，
		// 3. 其他.
		// 接下来的逻辑会对这三种BeanFactoryPostProcessor分别处理
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			//排除上面已经执行过的processor
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			//筛选优先级processor
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			//筛选实现排序的processor
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			//剩下默认为普通processor
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// 1. 执行Bean形式 BeanFactoryPostProcessor + PriorityOrdered
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		//2. 先排序，然后执行ordered processor
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		//最后调用普通的BeanFactoryPostProcessor
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		//清除缓存
		beanFactory.clearMetadataCache();
	}

	/**
	 * 注册后置处理器
	 * @param beanFactory
	 * @param applicationContext
	 */
	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
		// 获取所有BeanPostProcessor类型的bean名称
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		//当Spring的配置中的后处理器还没有被注册就已经开始了bean的初始化便会打印出BeanPostProcessorChecker中设定的信息
		// 注册BeanPostProcessorChecker，它可以打印BeanPostProcessor实例化过程中创建bean的日志信息
		// 计算已注册的bean + 1 +上面获取到的bean的数量，1是即将创建的BeanPostProcessorChecker
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		// 注册一个BeanPostProcessorChecker，用来记录bean在BeanPostProcessor实例化时的信息。
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// 按照优先的、内部的、排序的、无排序的进行分组排序，
		// 其中优先bean多了MergedBeanDefinitionPostProcessor类型(merged bean definition)
		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				// 如果BeanPostProcessors也实现了MergedBeanDefinitionPostProcessor接口，加入internalPostProcessors
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		//以下这些就是针对不同类型的后置处理器分别进行注册实例化
		// First, register the BeanPostProcessors that implement PriorityOrdered.
		//1.实现了优先级接口
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		//2.实现了排序接口
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			//AspectJAwareAdvisorAutoProxyCreator的实例化就是在这里进行的
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		//3.注册其它所有的BeanPostProcessor
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		//最后注册，所有的internal，也就是实现MergedBeanDefinitionPostProcessor的BeanPostProcessors
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		// 重新注册用于检测实现了ApplicationListener接口的内部bean的post processor,
		// 把它放到processor链的尾部
		// 注册BeanPostProcessor：ApplicationListenerDetector，这个是AbstractApplicationContext的内部类
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	/**
	 * 后置处理器排序
	 * @param postProcessors
	 * @param beanFactory
	 */
	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 * 调用BeanDefinition的后置处理器
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 * 调用BeanFactory的后置处理器
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
