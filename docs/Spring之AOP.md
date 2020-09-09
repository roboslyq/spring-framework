# Spring5.1.x之AOP

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

> 在spring bean生命周期中，在后置处理器beanPostProcessor时进行增强，实现代理。

## **AspectJAwareAdvisorAutoProxyCreator**

> AspectJAwareAdvisorAutoProxyCreator是一个后置处理器，它的作用是在bean对象实例化的前后可以进行一些操作。

# 四、AOP执行阶段

https://www.cnblogs.com/51life/p/9482734.html