# Spring5.1.x之AOP

> **AOP**：面向切面编程,是Spring在OOP基础上，提供的另一种编程能力。是可选项，不是必选项。在spring boot中，默认是开启此功能的。
>
> Java是一种静态语言，在运行时不方便修改Class，这在OOP编程中是一大缺陷。Spring提供了AOP功能，可以在运行时通过字节码提升(enhancher)来实现一些通用的功能，比如日志，监控，统计，事务，鉴权等公共功能。

# 一、配置方式

## 1、ProxyFactoryBean

```xml
<bean name="myController" class="org.springframework.aop.framework.ProxyFactoryBean">  
    <property name="interceptorNames">  
        <list>  
            <value>pointcut.advisor2</value>  
            <value>pointcut.advisor1</value>  
            <value>myRawController</value>  
        </list>  
    </property>  
</bean>  
```

## 2、BeanNameAutoProxyCreator 

```xml
<bean id="userService" class="com.aop.service.UserService"/>  
<bean id="beforeAdvice" class="com.aop.advice.BeforeAdvice"/>  
<bean id="xxxxxx" class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">  
        <property value="beanNames">  
               <list>  
                      <value>*service</value>  
               </list>  
        </property>  
        <property value="interceptorNames">  
               <value>beforeAdvice</value>  
        </property>  
</bean>  
```

>  这个类实现了BeanPostProcessor接口的子接口：SmartInstantiationAwareBeanPostProcessor，
>
>    每个被这个类care的类在取得bean实例前，会调用以下方法：
>
>   **public** Object postProcessBeforeInstantiation(Class beanClass, String beanName) 

## 3、AopNamespaceHandler

```xml
 <bean id="fooService" class="DefaultFooService"/>  
  <!-- this is the actual advice itself -->  
  <bean id="profiler" class="SimpleProfiler"/>  
  <aop:config>  
  	<aop:aspect ref="profiler">  
 		<aop:pointcut id="aopafterMethod"   expression="execution(* FooService.*(..))"/>  
		<aop:after pointcut-ref="aopafterMethod"  method="afterMethod"/>  
		<aop:pointcut id="aopBefore" expression="execution(* FooService.getBefore(String)) and args(myName)"/>  
   		<aop:before pointcut-ref="aopBefore"  method="beforeMethod"/>  
	</aop:aspect>  
</aop:config>  
```

> 这种配置方式的原理则是在进行配置文件解析的时候，由AopNameSpaceHandler对此标签进行解析，然后
>
>   注册一个“org.springframework.aop.config.internalAutoProxyCreator” bean,这个bean的实现类是：
>
>   org/springframework/aop/aspectj/autoproxy/AspectJAwareAdvisorAutoProxyCreator，此类也实现了
>
>   BeanPostProcessor接口。



通过NamespaceHandler接口，实现自定义标签解析。即AOP相关配置，非Spring原生标签。与Dubbo类型，是一个扩展标签。

```java
// aop标签解析的handler
public class AopNamespaceHandler extends NamespaceHandlerSupport {

	/**
	 * Register the {@link BeanDefinitionParser BeanDefinitionParsers} for the
	 * '{@code config}', '{@code spring-configured}', '{@code aspectj-autoproxy}'
	 * and '{@code scoped-proxy}' tags.
	 *
	 * roboslyq --> AOP配置解析阶段，将配置文件转换成BeanDefinition
	 */
	@Override
	public void init() {
		/**
		 * AOP配置DEMO
		 *  <aop:config>  ---->第一层标签config
		 * 	 <aop:aspect id="aopId" ref="sprinbBeanId"> ---->第二层标签aspect
		 * 		 <aop:pointcut id="pointcutId" expression="execution(* com.roboslyq.cn.*.*(..))" />
		 * 		 <aop:before method="methodName" pointcut-ref="pointcutId" />
		 * 		 <aop:after method="methodName2" pointcut-ref="pointcutId" />
		 * 	 </aop:aspect>
		 * </aop:config>
		 */
		// In 2.0 XSD as well as in 2.1 XSD.
		registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
		registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
		registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());

		// Only in 2.0 XSD: moved to context namespace as of 2.1
		registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
	}

}
```

	### ConfigBeanDefinitionParser

### AspectJAutoProxyBeanDefinitionParser

### ScopedProxyBeanDefinitionDecorator



# 二、AOP启动配置



## 注解@EnableAspectJAutoProxy

@Import(AspectJAutoProxyRegistrar.class)

AspectJAutoProxyRegistrar

# 三、生成代理Bean

> 在spring bean生命周期中，在后置处理器org/springframework/aop/aspectj/autoproxy/AspectJAwareAdvisorAutoProxyCreator时进行增强，实现代理。

## **AspectJAwareAdvisorAutoProxyCreator**

> AspectJAwareAdvisorAutoProxyCreator是一个后置处理器，它的作用是在bean对象实例化的前后可以进行一些操作。其是底层是依赖于JDK动态代理或者Cglib动态代理。

# 四、AOP执行阶段

https://www.cnblogs.com/51life/p/9482734.html

# 五、注解驱动栈分析

> 前面原理流程完全一样，只是后面的AbstractAutoProxyCreator对应的对象是org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator

```
wrapIfNecessary:377, AbstractAutoProxyCreator (org.springframework.aop.framework.autoproxy)
postProcessAfterInitialization:320, AbstractAutoProxyCreator (org.springframework.aop.framework.autoproxy)
applyBeanPostProcessorsAfterInitialization:458, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
initializeBean:1877, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
doCreateBean:643, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:537, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:392, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 942518407 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$8)
getSingleton:259, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:389, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:204, AbstractBeanFactory (org.springframework.beans.factory.support)
preInstantiateSingletons:811, DefaultListableBeanFactory (org.springframework.beans.factory.support)
finishBeanFactoryInitialization:1005, AbstractApplicationContext (org.springframework.context.support)
refresh:634, AbstractApplicationContext (org.springframework.context.support)
<init>:92, AnnotationConfigApplicationContext (org.springframework.context.annotation)
main:30, AopDemo (com.roboslyq.learn.aop)
```

-  Context刷新refresh()

  - AbstractApplicationContext#finishBeanFactoryInitialization 结束刷新

    - 结束刷新后，触发单例Bean实例化preInstantiateSingletons()

      - 实例化单例Bean：getBean(),doGetBean(),getSingleton(),lambda$doGetBean(),createBean(),doCreateBean()

        - 实例化完成后，初始化单例Bean:initializeBean()

          - 调用applyBeanPostProcessorsAfterInitialization()生命周期

            - AbstractAutoProxyCreator(AnnotationAwareAspectJAutoProxyCreator)的wrapIfNecessary()方法

              ```java
              protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
              		/**
              		 * 是一些不需要生成代理的场景判断
              		 */
              		if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
              			return bean;
              		}
              		if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
              			return bean;
              		}
              		if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
              			this.advisedBeans.put(cacheKey, Boolean.FALSE);
              			return bean;
              		}
              
              		// Create proxy if we have advice.
              		/**
              		 * 为目标bean查找匹配的通知器
              		 * 1、获取需要生成代理的Bean对象 ，在getAdvicesAndAdvisorsForBean中实现。
              		 * 2、通过获取Bendifinition中的List<Advisor>来判断,找出符合当前beanName的对应的@PointCut注解修饰的方法。即切点。
              		 */
              		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
              		// 如果存在切点，则需要AOP代理
              		if (specificInterceptors != DO_NOT_PROXY) {
              			this.advisedBeans.put(cacheKey, Boolean.TRUE);
              			/**
              			 * 创建代理：如果通知器的数组specificInterceptors不为空(即存在对应的切面，并且当前Bean符合被切面代理的条件)，那么生成代理对象
              			 * 			 有两种生成方式生成代理：JDK动态代理和cglib代理，默认是JDK动态代理
              			 */
              			Object proxy = createProxy(
              					bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
              			this.proxyTypes.put(cacheKey, proxy.getClass());
              			//返回代理对象，此时容器中的beanName对应的对象不再是目标对象，而是代理对象。
              			return proxy;
              		}
              		// 如果不存在切点，表示不需要代理，直接返回 
              		this.advisedBeans.put(cacheKey, Boolean.FALSE);
              		return bean;
              	}
              ```

              

# 相关类

- AnnotationAwareAspectJAutoProxyCreator
  - 此类为BeanPostProcessor类

- ProxyFactory

  - 通过new ProxyFactory()。父类为ProxyCreatorSupport

- DefaultAopProxyFactory

  - 手动创建，通过ProxyFacotory创建

- AopProxy

  - 通过DefaultAopProxyFactory创建，主要有两种实现JdkDynamicAopProxy和ObjenesisCglibAopProxy

  
