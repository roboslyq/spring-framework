# Spring5.1.x之AOP

# Spring Aop之BeanDefinition解析

> 通过NamespaceHandler接口，实现自定义标签解析。即AOP相关配置，非Spring原生标签。与Dubbo类型，是一个扩展标签。

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

# 生成代理Bean

## **AspectJAwareAdvisorAutoProxyCreator**

> AspectJAwareAdvisorAutoProxyCreator是一个后置处理器，它的作用是在bean对象实例化的前后可以进行一些操作。

# AOP执行阶段

https://www.cnblogs.com/51life/p/9482734.html



# 配置入口

## 1、@EnableAspectJAutoProxy

## 2、@Import(AspectJAutoProxyRegistrar.class)

## 3、AspectJAutoProxyRegistrar