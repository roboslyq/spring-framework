# Spring5.1.x之AOP

# 前言

​	Java是一种静态语言，在运行时不方便修改Class，这在OOP编程中是一大缺陷。Spring提供了AOP功能，可以在运行时通过字节码提升(enhancher)来实现一些通用的功能，比如日志，监控，统计，事务，鉴权等公共功能。

​	**AOP**：面向切面编程,是Spring在OOP基础上，提供的另一种编程能力，通过这种能力，我们很容易开发出通用的比如日志，监控，统计，事务，鉴权等公共功能，而且代码不重复。但此功能是可选项，不是必选项。在spring boot中，默认是开启此功能的。

1、Spring Aop是基于spring bean ,context等基础组件之上，提供的一种额外的面向aop的编程能力。方便应用开发AOP编程的一种模式。

2、Spring Aop不是必须的，是可选的。但目前应用大多开启了这一特性，尤其是spring boot，因为这一特性用的实在是太普遍了。

3、AspectJ是java AOP界最强的，也是最完善最完美的，同时也是最复杂的。Spring Aop借鉴了此设计，并且直接引用了其部分功能实现。比如AspectJ编程风格，但仅仅是引用了这种编程风格(注解)，底层完全不依赖于AspectJ。

4、Spring实现AOP也引用[aopalliance](https://sourceforge.net/projects/aopalliance/)框架的实现，Aop Alliance项目是许多对Aop和java有浓厚兴趣的软件开发人员联合成立的开源项目，其提供的源码都是完全免费的(Public Domain).官方网站http://aopalliance.sourceforge.net/。

![image-20211125091610484](Spring%E4%B9%8BAOP/image-20211125091610484.png)

5、Spring AOP底层也使用了Objenesis技术，Objenesis是一款轻量级的Java类库。主要用来创建特定的对象。由于不是所有的类都有无参构造器又或者类构造器是private，在这样的情况下，如果我们还想实例化对象，class.newInstance是无法满足的。

其官网地址：http://objenesis.org/，github地址：https://github.com/easymock/objenesis

![image-20211128085709583](Spring%E4%B9%8BAOP/image-20211128085709583.png)

6、Spring中，如果Bean不需要切面功能，则直接是原始的Bean对像。

​	如果Bean实现了接口，并且符合切面条件，则是使用Jdk动态代理生成的对象

​	如果Bean没有实现接口，并且符合切面条件，则是使用cglib动态代理生成的对象。

# 一、XML配置方式

## 1.1、配置ProxyFactoryBean

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

## 1.2、配置BeanNameAutoProxyCreator 

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

## 1.3、配置AopNamespaceHandler

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
>   org/springframework/aop/aspectj/autoproxy/**AspectJAwareAdvisorAutoProxyCreator**，此类也实现了
>
>   BeanPostProcessor接口。



通过NamespaceHandler接口，实现自定义标签解析。即AOP相关配置，非Spring原生标签。与Dubbo类型，是一个扩展标签。

```java
// aop标签解析的handler
public class AopNamespaceHandler extends NamespaceHandlerSupport {
	/**
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

## 1.4、运行

TODO



# 二、注解模式

## 2.1 定义接口

```java
package com.roboslyq.learn.aop;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/23
 * @since 1.0.0
 */
public interface UserDao {
   void addUser();
   void deleteUser();
}
```

## 2.2 定义实现

```java
package com.roboslyq.learn.aop;

/**
 *
 * 〈〉
 * @author roboslyq
 * @date 2021/11/23
 * @since 1.0.0
 */
public class UserDaoImpl implements UserDao{

   public void addUser() {
      System.out.println("add user ");
   }

   public void deleteUser() {
      System.out.println("delete user ");
   }

}
```

## 2.3 定义切面

```java
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
```

## 2.4 测试

```java
package com.roboslyq.learn.aop;

import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *
 * 〈切面编程〉
 * @author roboslyq
 * @date 2021/11/8
 * @since 1.0.0
 */
@EnableAspectJAutoProxy
public class AopDemoMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopDemoMain.class);
		UserDao userDao = context.getBean(UserDao.class);
		System.out.println(userDao.getClass().getSuperclass());
		userDao.addUser();
		context.close();
	}

	@Bean
	public AspectJAwareAdvisorAutoProxyCreator initAspect(){
		return new AspectJAwareAdvisorAutoProxyCreator();
	}

	@Bean
	public UserDao userDao(){
		return new UserDaoImpl();
	}
	
	@Bean
	public Logger logger(){
		return new Logger();
	}

}
```

打印结果：

![image-20211128091011206](Spring%E4%B9%8BAOP/image-20211128091011206.png)

- 注解@EnableAspectJAutoProxy
  - @Import(AspectJAutoProxyRegistrar.class)
  - AspectJAutoProxyRegistrar

- 生成代理Bean

  - 在spring bean生命周期中，在后置处理器org/springframework/aop/aspectj/autoproxy/AspectJAwareAdvisorAutoProxyCreator时进行增强，实现代理。

- **AspectJAwareAdvisorAutoProxyCreator**

  AspectJAwareAdvisorAutoProxyCreator是一个后置处理器，它的作用是在bean对象实例化的前后可以进行一些操作。其是底层是依赖于JDK动态代理或者Cglib动态代理。

- AnnotationAwareAspectJAutoProxyCreator(注解时)

  当存在此注解上，类`class org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator`不生效，因为`AnnotationAwareAspectJAutoProxyCreator`的优先级高于AspectJAwareAdvisorAutoProxyCreator。

# 三、设计模式

## 3.1、代理模式

- Java 动态代理
  - JDK 动态代理
  - 字节码提升，如 CGLIB
- 静态代理 
  - 常用 OOP 继承和组合相结合

## 3.1、拦截器模式

在代理模式下，实现拦截器。在目标方法执行前后进行相关操作。

- 拦截类型
  - 前置拦截（Before）
  - 后置拦截（After）
  - 异常拦截（Exception）

## 3.3、判断模式

- 判断来源
  - 类型（Class）
  - 方法（Method）
  - 注解（Annotation）
  - 参数（Parameter）
  - 异常（Exception）

> 判断模式主要是通过反射获取类型，方法，注解等信息，来判断是否符合代理相关条件。主要类为`ReflectionUtils`

# 四、基本概念

## 4.1、核心特性

- 纯 Java 实现、无编译时特殊处理、不修改和控制 ClassLoader。与AspectJ编译时增强不一样。
- 仅支持方法级别的 Join Points
- 非完整 AOP 实现框架
- Spring IoC 容器整合•AspectJ 注解驱动整合（非竞争关系）

## 4.2、基本概念

**二、AOP核心概念**

**1、横切关注点**

对哪些方法进行拦截，拦截后怎么处理，这些关注点称之为横切关注点

**2、切面（aspect）**

类是对物体特征的抽象，切面就是对横切关注点的抽象

**3、连接点（joinpoint）**

被拦截到的点，因为Spring只支持方法类型的连接点，所以在Spring中连接点指的就是被拦截到的方法，实际上连接点还可以是字段或者构造器。

- Interceptor 执行上下文 - Invocation
  - 方法拦截器执行上下文 - MethodInvocation
  - 构造器拦截器执行上下文 - ConstructorInvocation
- MethodInvocation 实现
  - 基于反射 - ReflectiveMethodInvocation 
  - 基于 CGLIB - CglibMethodInvocation

**4、切入点（pointcut）**

对连接点进行拦截的定义

- XML 配置: <aop:pointcut />
- 核心 API - org.springframework.aop.Pointcut
  - org.springframework.aop.**ClassFilter**
  - org.springframework.aop.**MethodMatcher**

- 适配实现 - DefaultPointcutAdvisor
- 组合实现 - org.springframework.aop.support.ComposablePointcut
- 工具类
  - ClassFilter 工具类 -ClassFilters
  - MethodMatcher 工具类 - MethodMatchers
  - Pointcut 工具类 - Pointcuts
- 常见便利实现
  - 静态 Pointcut - StaticMethodMatcherPointcut
  - 正则表达式 Pointcut - JdkRegexpMethodPointcut
  - 控制流 Pointcut - ControlFlowPointcut
  - AspectJ 实现 - org.springframework.aop.aspectj.AspectJExpressionPointcut

**5、通知（advice）**

所谓通知指的就是指拦截到连接点之后要执行的代码，通知分为前置、后置、异常、最终、环绕通知五类

- Around Advice - Interceptor

  - 方法拦截器 - MethodInterceptor
  - 构造器拦截器 - ConstructorInterceptor

- 前置动作

  - 标准接口 - org.springframework.aop.BeforeAdvice
  - 方法级别 - org.springframework.aop.MethodBeforeAdvice
  - 实现：org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor
  - 实现：org.springframework.aop.aspectj.AspectJMethodBeforeAdvice

- 后置动作

  - org.springframework.aop.AfterAdvice
  - org.springframework.aop.AfterReturningAdvice
  - org.springframework.aop.ThrowsAdvice

  标准实现：

  org.springframework.aop.framework.adapter.ThrowsAdviceInterceptor

  org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor

  Aspectj实现：

  org.springframework.aop.aspectj.AspectJAfterAdvice

  org.springframework.aop.aspectj.AspectJAfterReturningAdvice

  org.springframework.aop.aspectj.AspectJAfterThrowingAdvice

**6、目标对象**(TargetSource)

代理的目标对象,也就是目标类

**7、织入（weave）**

将切面应用到目标对象并导致代理对象创建的过程

**8、引入（introduction）**

在不修改代码的前提下，引入可以在运行期为类动态地添加一些方法或字段

**9、Advice容器(Advisor)**

- 接口 - org.springframework.aop.Advisor
  - 通用实现 - org.springframework.aop.support.DefaultPointcutAdvisor

**10、Pointcut 与 Advice 连接器 - PointcutAdvisor**

- 接口 - org.springframework.aop.PointcutAdvisor
  - 通用实现
    - org.springframework.aop.support.DefaultPointcutAdvisor
  - AspectJ 实现
    - org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor
    - org.springframework.aop.aspectj.AspectJPointcutAdvisor
  - 静态方法实现
    - org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor
  - IoC 容器实现
    - org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor

**11、Advisor 的 Interceptor 适配器**

接口 - org.springframework.aop.framework.adapter.AdvisorAdapter

MethodBeforeAdvice 实现

​	•org.springframework.aop.framework.adapter.MethodBeforeAdviceAdapter

•AfterReturningAdvice 实现

​	•org.springframework.aop.framework.adapter.AfterReturningAdviceAdapter

•ThrowsAdvice 实现

​	•org.springframework.aop.framework.adapter.ThrowsAdviceAdapter

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

              

# 六、相关类

- AnnotationAwareAspectJAutoProxyCreator
  - 此类为BeanPostProcessor类

- ProxyFactory

  - 通过new ProxyFactory()。父类为ProxyCreatorSupport

- DefaultAopProxyFactory

  - 手动创建，通过ProxyFacotory创建

- AopProxy

  - 通过DefaultAopProxyFactory创建，主要有两种实现JdkDynamicAopProxy和ObjenesisCglibAopProxy

  

# 七、动态生成的类

## Jdk动态生成的类

> 关键的Class签名信息：`class $Proxy24 extends Proxy implements BeanNameAware, BeanFactoryAware, ITestBean, IOther, Comparable, SpringProxy, Advised, DecoratingProxy `
>
> 即代理类是通过实现业务`ITestBean`接口，然后继承JDK中的Proxy接口，来完成代理功能的。

```java
public final class $Proxy24 extends Proxy implements BeanNameAware, BeanFactoryAware, ITestBean, IOther, Comparable, SpringProxy, Advised, DecoratingProxy {
    private static Method m1;
    private static Method m48;
    private static Method m15;
    private static Method m22;
    private static Method m40;
     // ......省略其它方法
    public $Proxy24(InvocationHandler var1) throws  {
        super(var1);
    }
    // ...... 省略其它方法
    public final int getAge() throws  {
        try {
            return (Integer)super.h.invoke(this, m29, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }
    public final int age() throws  {
        try {
            return (Integer)super.h.invoke(this, m27, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }
    
    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m48 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isExposeProxy");
            m15 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getLawyer");
            m22 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setNestedIntArray", Class.forName("[[I"));
            m40 = Class.forName("org.springframework.aop.framework.Advised").getMethod("removeAdvisor", Integer.TYPE);
            m35 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getProxiedInterfaces");
            m8 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getSpouse");
            m4 = Class.forName("org.springframework.beans.factory.BeanFactoryAware").getMethod("setBeanFactory", Class.forName("org.springframework.beans.factory.BeanFactory"));
            m12 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getSomeIntArray");
            m37 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isInterfaceProxied", Class.forName("java.lang.Class"));
            m42 = Class.forName("org.springframework.aop.framework.Advised").getMethod("removeAdvice", Class.forName("org.aopalliance.aop.Advice"));
            m23 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getNestedIntegerArray");
            m5 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getName");
            m21 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setNestedIntegerArray", Class.forName("[[Ljava.lang.Integer;"));
            m52 = Class.forName("org.springframework.aop.framework.Advised").getMethod("setExposeProxy", Boolean.TYPE);
            m2 = Class.forName("java.lang.Object").getMethod("toString");
            m11 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setSpouse", Class.forName("org.springframework.tests.sample.beans.ITestBean"));
            m32 = Class.forName("org.springframework.aop.framework.Advised").getMethod("indexOf", Class.forName("org.aopalliance.aop.Advice"));
            m34 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isFrozen");
            m24 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getNestedIntArray");
            m29 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getAge");
            m43 = Class.forName("org.springframework.aop.framework.Advised").getMethod("replaceAdvisor", Class.forName("org.springframework.aop.Advisor"), Class.forName("org.springframework.aop.Advisor"));
            m50 = Class.forName("org.springframework.aop.framework.Advised").getMethod("setPreFiltered", Boolean.TYPE);
            m7 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getStringArray");
            m17 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("unreliableFileOperation");
            m44 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvisor", Class.forName("org.springframework.aop.Advisor"));
            m53 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isProxyTargetClass");
            m19 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getSomeIntegerArray");
            m27 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("age");
            m14 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("returnsThis");
            m33 = Class.forName("org.springframework.aop.framework.Advised").getMethod("indexOf", Class.forName("org.springframework.aop.Advisor"));
            m49 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getTargetSource");
            m39 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvice", Integer.TYPE, Class.forName("org.aopalliance.aop.Advice"));
            m38 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvice", Class.forName("org.aopalliance.aop.Advice"));
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
            m18 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getNestedIndexedBean");
            m26 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("exceptional", Class.forName("java.lang.Throwable"));
            m25 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("haveBirthday");
            m51 = Class.forName("org.springframework.aop.framework.Advised").getMethod("setTargetSource", Class.forName("org.springframework.aop.TargetSource"));
            m6 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setName", Class.forName("java.lang.String"));
            m3 = Class.forName("org.springframework.beans.factory.BeanNameAware").getMethod("setBeanName", Class.forName("java.lang.String"));
            m9 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getSpouses");
            m20 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setSomeIntegerArray", Class.forName("[Ljava.lang.Integer;"));
            m54 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getTargetClass");
            m28 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setAge", Integer.TYPE);
            m45 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvisor", Integer.TYPE, Class.forName("org.springframework.aop.Advisor"));
            m55 = Class.forName("org.springframework.core.DecoratingProxy").getMethod("getDecoratedClass");
            m41 = Class.forName("org.springframework.aop.framework.Advised").getMethod("removeAdvisor", Class.forName("org.springframework.aop.Advisor"));
            m13 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setSomeIntArray", Class.forName("[I"));
            m31 = Class.forName("java.lang.Comparable").getMethod("compareTo", Class.forName("java.lang.Object"));
            m10 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("setStringArray", Class.forName("[Ljava.lang.String;"));
            m30 = Class.forName("org.springframework.tests.sample.beans.IOther").getMethod("absquatulate");
            m36 = Class.forName("org.springframework.aop.framework.Advised").getMethod("toProxyConfigString");
            m16 = Class.forName("org.springframework.tests.sample.beans.ITestBean").getMethod("getDoctor");
            m47 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getAdvisors");
            m46 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isPreFiltered");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}
```

总体来说，所有的方法均是通过`super.h.invoke(this, m29, (Object[])null);`来实现。

> Proxy是JDK中的Proxy类，其中有 protected InvocationHandler h;引用

```java
package java.lang.reflect;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import sun.misc.ProxyGenerator;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class Proxy implements java.io.Serializable {

    private static final long serialVersionUID = -2222568056686623797L;

    /** parameter types of a proxy class constructor */
    private static final Class<?>[] constructorParams =
        { InvocationHandler.class };
    private static final WeakCache<ClassLoader, Class<?>[], Class<?>>
        proxyClassCache = new WeakCache<>(new KeyFactory(), new ProxyClassFactory());
    protected InvocationHandler h;
    private Proxy() {
    }
    protected Proxy(InvocationHandler h) {
        Objects.requireNonNull(h);
        this.h = h;
    }
    @CallerSensitive
    public static Class<?> getProxyClass(ClassLoader loader,
                                         Class<?>... interfaces)
        throws IllegalArgumentException
    {
        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }
        return getProxyClass0(loader, intfs);
    }
    private static void checkProxyAccess(Class<?> caller,
                                         ClassLoader loader,
                                         Class<?>... interfaces)
    {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ClassLoader ccl = caller.getClassLoader();
            if (VM.isSystemDomainLoader(loader) && !VM.isSystemDomainLoader(ccl)) {
                sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            }
            ReflectUtil.checkProxyPackageAccess(ccl, interfaces);
        }
    }
    private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        return proxyClassCache.get(loader, interfaces);
    }

    /*
     * a key used for proxy class with 0 implemented interfaces
     */
    private static final Object key0 = new Object();

    /*
     * Key1 and Key2 are optimized for the common use of dynamic proxies
     * that implement 1 or 2 interfaces.
     */

    /*
     * a key used for proxy class with 1 implemented interface
     */
    private static final class Key1 extends WeakReference<Class<?>> {
        private final int hash;

        Key1(Class<?> intf) {
            super(intf);
            this.hash = intf.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            Class<?> intf;
            return this == obj ||
                   obj != null &&
                   obj.getClass() == Key1.class &&
                   (intf = get()) != null &&
                   intf == ((Key1) obj).get();
        }
    }

    /*
     * a key used for proxy class with 2 implemented interfaces
     */
    private static final class Key2 extends WeakReference<Class<?>> {
        private final int hash;
        private final WeakReference<Class<?>> ref2;

        Key2(Class<?> intf1, Class<?> intf2) {
            super(intf1);
            hash = 31 * intf1.hashCode() + intf2.hashCode();
            ref2 = new WeakReference<Class<?>>(intf2);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            Class<?> intf1, intf2;
            return this == obj ||
                   obj != null &&
                   obj.getClass() == Key2.class &&
                   (intf1 = get()) != null &&
                   intf1 == ((Key2) obj).get() &&
                   (intf2 = ref2.get()) != null &&
                   intf2 == ((Key2) obj).ref2.get();
        }
    }

    /*
     * a key used for proxy class with any number of implemented interfaces
     * (used here for 3 or more only)
     */
    private static final class KeyX {
        private final int hash;
        private final WeakReference<Class<?>>[] refs;

        @SuppressWarnings("unchecked")
        KeyX(Class<?>[] interfaces) {
            hash = Arrays.hashCode(interfaces);
            refs = (WeakReference<Class<?>>[])new WeakReference<?>[interfaces.length];
            for (int i = 0; i < interfaces.length; i++) {
                refs[i] = new WeakReference<>(interfaces[i]);
            }
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj ||
                   obj != null &&
                   obj.getClass() == KeyX.class &&
                   equals(refs, ((KeyX) obj).refs);
        }

        private static boolean equals(WeakReference<Class<?>>[] refs1,
                                      WeakReference<Class<?>>[] refs2) {
            if (refs1.length != refs2.length) {
                return false;
            }
            for (int i = 0; i < refs1.length; i++) {
                Class<?> intf = refs1[i].get();
                if (intf == null || intf != refs2[i].get()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * A function that maps an array of interfaces to an optimal key where
     * Class objects representing interfaces are weakly referenced.
     */
    private static final class KeyFactory
        implements BiFunction<ClassLoader, Class<?>[], Object>
    {
        @Override
        public Object apply(ClassLoader classLoader, Class<?>[] interfaces) {
            switch (interfaces.length) {
                case 1: return new Key1(interfaces[0]); // the most frequent
                case 2: return new Key2(interfaces[0], interfaces[1]);
                case 0: return key0;
                default: return new KeyX(interfaces);
            }
        }
    }

    /**
     * A factory function that generates, defines and returns the proxy class given
     * the ClassLoader and array of interfaces.
     */
    private static final class ProxyClassFactory
        implements BiFunction<ClassLoader, Class<?>[], Class<?>>
    {
        // prefix for all proxy class names
        private static final String proxyClassNamePrefix = "$Proxy";

        // next number to use for generation of unique proxy class names
        private static final AtomicLong nextUniqueNumber = new AtomicLong();

        @Override
        public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {

            Map<Class<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);
            for (Class<?> intf : interfaces) {
                /*
                 * Verify that the class loader resolves the name of this
                 * interface to the same Class object.
                 */
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = Class.forName(intf.getName(), false, loader);
                } catch (ClassNotFoundException e) {
                }
                if (interfaceClass != intf) {
                    throw new IllegalArgumentException(
                        intf + " is not visible from class loader");
                }
                /*
                 * Verify that the Class object actually represents an
                 * interface.
                 */
                if (!interfaceClass.isInterface()) {
                    throw new IllegalArgumentException(
                        interfaceClass.getName() + " is not an interface");
                }
                /*
                 * Verify that this interface is not a duplicate.
                 */
                if (interfaceSet.put(interfaceClass, Boolean.TRUE) != null) {
                    throw new IllegalArgumentException(
                        "repeated interface: " + interfaceClass.getName());
                }
            }

            String proxyPkg = null;     // package to define proxy class in
            int accessFlags = Modifier.PUBLIC | Modifier.FINAL;


            for (Class<?> intf : interfaces) {
                int flags = intf.getModifiers();
                if (!Modifier.isPublic(flags)) {
                    accessFlags = Modifier.FINAL;
                    String name = intf.getName();
                    int n = name.lastIndexOf('.');
                    String pkg = ((n == -1) ? "" : name.substring(0, n + 1));
                    if (proxyPkg == null) {
                        proxyPkg = pkg;
                    } else if (!pkg.equals(proxyPkg)) {
                        throw new IllegalArgumentException(
                            "non-public interfaces from different packages");
                    }
                }
            }

            if (proxyPkg == null) {
                // if no non-public proxy interfaces, use com.sun.proxy package
                proxyPkg = ReflectUtil.PROXY_PACKAGE + ".";
            }

            /*
             * Choose a name for the proxy class to generate.
             */
            long num = nextUniqueNumber.getAndIncrement();
            String proxyName = proxyPkg + proxyClassNamePrefix + num;

            /*
             * Generate the specified proxy class.
             */
            byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                proxyName, interfaces, accessFlags);
            try {
                return defineClass0(loader, proxyName,
                                    proxyClassFile, 0, proxyClassFile.length);
            } catch (ClassFormatError e) {
                throw new IllegalArgumentException(e.toString());
            }
        }
    }

    @CallerSensitive
    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        Objects.requireNonNull(h);

        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        /*
         * Look up or generate the designated proxy class.
         */
        Class<?> cl = getProxyClass0(loader, intfs);

        /*
         * Invoke its constructor with the designated invocation handler.
         */
        try {
            if (sm != null) {
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }

            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
            return cons.newInstance(new Object[]{h});
        } catch (IllegalAccessException|InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString(), e);
        }
    }

    private static void checkNewProxyPermission(Class<?> caller, Class<?> proxyClass) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            if (ReflectUtil.isNonPublicProxyClass(proxyClass)) {
                ClassLoader ccl = caller.getClassLoader();
                ClassLoader pcl = proxyClass.getClassLoader();

                // do permission check if the caller is in a different runtime package
                // of the proxy class
                int n = proxyClass.getName().lastIndexOf('.');
                String pkg = (n == -1) ? "" : proxyClass.getName().substring(0, n);

                n = caller.getName().lastIndexOf('.');
                String callerPkg = (n == -1) ? "" : caller.getName().substring(0, n);

                if (pcl != ccl || !pkg.equals(callerPkg)) {
                    sm.checkPermission(new ReflectPermission("newProxyInPackage." + pkg));
                }
            }
        }
    }

    public static boolean isProxyClass(Class<?> cl) {
        return Proxy.class.isAssignableFrom(cl) && proxyClassCache.containsValue(cl);
    }

    @CallerSensitive
    public static InvocationHandler getInvocationHandler(Object proxy)
        throws IllegalArgumentException
    {
        /*
         * Verify that the object is actually a proxy instance.
         */
        if (!isProxyClass(proxy.getClass())) {
            throw new IllegalArgumentException("not a proxy instance");
        }

        final Proxy p = (Proxy) proxy;
        final InvocationHandler ih = p.h;
        if (System.getSecurityManager() != null) {
            Class<?> ihClass = ih.getClass();
            Class<?> caller = Reflection.getCallerClass();
            if (ReflectUtil.needsPackageAccessCheck(caller.getClassLoader(),
                                                    ihClass.getClassLoader()))
            {
                ReflectUtil.checkPackageAccess(ihClass);
            }
        }

        return ih;
    }

    private static native Class<?> defineClass0(ClassLoader loader, String name,
                                                byte[] b, int off, int len);
}
```

## Cglib生成的类

> 关签名信息，当前业务UserDaoNoInterface没有实现接口，因此只能通过继承其来实现代理扩展。完成与JDK的代理无关。

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.roboslyq.learn.aop;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

public class UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35 extends UserDaoNoInterface implements SpringProxy, Advised, Factory {
    private boolean CGLIB$BOUND;
    public static Object CGLIB$FACTORY_DATA;
    private static final ThreadLocal CGLIB$THREAD_CALLBACKS;
    private static final Callback[] CGLIB$STATIC_CALLBACKS;
    private MethodInterceptor CGLIB$CALLBACK_0;
    private MethodInterceptor CGLIB$CALLBACK_1;
    private NoOp CGLIB$CALLBACK_2;
    private Dispatcher CGLIB$CALLBACK_3;
    private Dispatcher CGLIB$CALLBACK_4;
    private MethodInterceptor CGLIB$CALLBACK_5;
    private MethodInterceptor CGLIB$CALLBACK_6;
    private static Object CGLIB$CALLBACK_FILTER;
    private static final Method CGLIB$deleteUser$0$Method;
    private static final MethodProxy CGLIB$deleteUser$0$Proxy;
    private static final Object[] CGLIB$emptyArgs;
    private static final Method CGLIB$addUser$1$Method;
    private static final MethodProxy CGLIB$addUser$1$Proxy;
    private static final Method CGLIB$equals$2$Method;
    private static final MethodProxy CGLIB$equals$2$Proxy;
    private static final Method CGLIB$toString$3$Method;
    private static final MethodProxy CGLIB$toString$3$Proxy;
    private static final Method CGLIB$hashCode$4$Method;
    private static final MethodProxy CGLIB$hashCode$4$Proxy;
    private static final Method CGLIB$clone$5$Method;
    private static final MethodProxy CGLIB$clone$5$Proxy;

    public UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35() {
        try {
            super();
            CGLIB$BIND_CALLBACKS(this);
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    static {
        CGLIB$STATICHOOK2();
        CGLIB$STATICHOOK1();
    }

    public final boolean equals(Object var1) {
        try {
            MethodInterceptor var10000 = this.CGLIB$CALLBACK_5;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_5;
            }

            if (var10000 != null) {
                Object var4 = var10000.intercept(this, CGLIB$equals$2$Method, new Object[]{var1}, CGLIB$equals$2$Proxy);
                return var4 == null ? false : (Boolean)var4;
            } else {
                return super.equals(var1);
            }
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String toString() {
        try {
            MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_0;
            }

            return var10000 != null ? (String)var10000.intercept(this, CGLIB$toString$3$Method, CGLIB$emptyArgs, CGLIB$toString$3$Proxy) : super.toString();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final int hashCode() {
        try {
            MethodInterceptor var10000 = this.CGLIB$CALLBACK_6;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_6;
            }

            if (var10000 != null) {
                Object var3 = var10000.intercept(this, CGLIB$hashCode$4$Method, CGLIB$emptyArgs, CGLIB$hashCode$4$Proxy);
                return var3 == null ? 0 : ((Number)var3).intValue();
            } else {
                return super.hashCode();
            }
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    protected final Object clone() throws CloneNotSupportedException {
        try {
            MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_0;
            }

            return var10000 != null ? var10000.intercept(this, CGLIB$clone$5$Method, CGLIB$emptyArgs, CGLIB$clone$5$Proxy) : super.clone();
        } catch (Error | CloneNotSupportedException | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final int indexOf(Advice var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).indexOf(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int indexOf(Advisor var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).indexOf(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public Object newInstance(Callback[] var1) {
        try {
            CGLIB$SET_THREAD_CALLBACKS(var1);
            UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35 var10000 = new UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35();
            CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
            return var10000;
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public Object newInstance(Callback var1) {
        try {
            throw new IllegalStateException("More than one callback object required");
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public Object newInstance(Class[] var1, Object[] var2, Callback[] var3) {
        try {
            CGLIB$SET_THREAD_CALLBACKS(var3);
            UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35 var10000 = new UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35;
            switch(var1.length) {
            case 0:
                var10000.<init>();
                CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
                return var10000;
            default:
                throw new IllegalArgumentException("Constructor not found");
            }
        } catch (Error | RuntimeException var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final boolean isFrozen() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).isFrozen();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final void deleteUser() {
        try {
            MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_0;
            }

            if (var10000 != null) {
                var10000.intercept(this, CGLIB$deleteUser$0$Method, CGLIB$emptyArgs, CGLIB$deleteUser$0$Proxy);
            } else {
                super.deleteUser();
            }
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final void addUser() {
        try {
            MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_0;
            }

            if (var10000 != null) {
                var10000.intercept(this, CGLIB$addUser$1$Method, CGLIB$emptyArgs, CGLIB$addUser$1$Proxy);
            } else {
                super.addUser();
            }
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final boolean isProxyTargetClass() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).isProxyTargetClass();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final void setTargetSource(TargetSource var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).setTargetSource(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final TargetSource getTargetSource() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).getTargetSource();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final void setPreFiltered(boolean var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).setPreFiltered(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean isExposeProxy() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).isExposeProxy();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final void setExposeProxy(boolean var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).setExposeProxy(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final Advisor[] getAdvisors() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).getAdvisors();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public static void CGLIB$SET_THREAD_CALLBACKS(Callback[] var0) {
        CGLIB$THREAD_CALLBACKS.set(var0);
    }

    public static void CGLIB$SET_STATIC_CALLBACKS(Callback[] var0) {
        CGLIB$STATIC_CALLBACKS = var0;
    }

    public void setCallbacks(Callback[] var1) {
        try {
            this.CGLIB$CALLBACK_0 = (MethodInterceptor)var1[0];
            this.CGLIB$CALLBACK_1 = (MethodInterceptor)var1[1];
            this.CGLIB$CALLBACK_2 = (NoOp)var1[2];
            this.CGLIB$CALLBACK_3 = (Dispatcher)var1[3];
            this.CGLIB$CALLBACK_4 = (Dispatcher)var1[4];
            this.CGLIB$CALLBACK_5 = (MethodInterceptor)var1[5];
            this.CGLIB$CALLBACK_6 = (MethodInterceptor)var1[6];
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public Callback[] getCallbacks() {
        try {
            CGLIB$BIND_CALLBACKS(this);
            return new Callback[]{this.CGLIB$CALLBACK_0, this.CGLIB$CALLBACK_1, this.CGLIB$CALLBACK_2, this.CGLIB$CALLBACK_3, this.CGLIB$CALLBACK_4, this.CGLIB$CALLBACK_5, this.CGLIB$CALLBACK_6};
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public void setCallback(int var1, Callback var2) {
        try {
            switch(var1) {
            case 0:
                this.CGLIB$CALLBACK_0 = (MethodInterceptor)var2;
                break;
            case 1:
                this.CGLIB$CALLBACK_1 = (MethodInterceptor)var2;
                break;
            case 2:
                this.CGLIB$CALLBACK_2 = (NoOp)var2;
                break;
            case 3:
                this.CGLIB$CALLBACK_3 = (Dispatcher)var2;
                break;
            case 4:
                this.CGLIB$CALLBACK_4 = (Dispatcher)var2;
                break;
            case 5:
                this.CGLIB$CALLBACK_5 = (MethodInterceptor)var2;
                break;
            case 6:
                this.CGLIB$CALLBACK_6 = (MethodInterceptor)var2;
            }

        } catch (Error | RuntimeException var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public Callback getCallback(int var1) {
        try {
            CGLIB$BIND_CALLBACKS(this);
            Object var10000;
            switch(var1) {
            case 0:
                var10000 = this.CGLIB$CALLBACK_0;
                break;
            case 1:
                var10000 = this.CGLIB$CALLBACK_1;
                break;
            case 2:
                var10000 = this.CGLIB$CALLBACK_2;
                break;
            case 3:
                var10000 = this.CGLIB$CALLBACK_3;
                break;
            case 4:
                var10000 = this.CGLIB$CALLBACK_4;
                break;
            case 5:
                var10000 = this.CGLIB$CALLBACK_5;
                break;
            case 6:
                var10000 = this.CGLIB$CALLBACK_6;
                break;
            default:
                var10000 = null;
            }

            return (Callback)var10000;
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    final void CGLIB$addUser$1() {
        super.addUser();
    }

    final int CGLIB$hashCode$4() {
        return super.hashCode();
    }

    final boolean CGLIB$equals$2(Object var1) {
        return super.equals(var1);
    }

    final String CGLIB$toString$3() {
        return super.toString();
    }

    final Object CGLIB$clone$5() throws CloneNotSupportedException {
        return super.clone();
    }

    final void CGLIB$deleteUser$0() {
        super.deleteUser();
    }

    private static final void CGLIB$BIND_CALLBACKS(Object var0) {
        UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35 var1 = (UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35)var0;
        if (!var1.CGLIB$BOUND) {
            var1.CGLIB$BOUND = true;
            Object var10000 = CGLIB$THREAD_CALLBACKS.get();
            if (var10000 == null) {
                var10000 = CGLIB$STATIC_CALLBACKS;
                if (var10000 == null) {
                    return;
                }
            }

            Callback[] var10001 = (Callback[])var10000;
            var1.CGLIB$CALLBACK_6 = (MethodInterceptor)((Callback[])var10000)[6];
            var1.CGLIB$CALLBACK_5 = (MethodInterceptor)var10001[5];
            var1.CGLIB$CALLBACK_4 = (Dispatcher)var10001[4];
            var1.CGLIB$CALLBACK_3 = (Dispatcher)var10001[3];
            var1.CGLIB$CALLBACK_2 = (NoOp)var10001[2];
            var1.CGLIB$CALLBACK_1 = (MethodInterceptor)var10001[1];
            var1.CGLIB$CALLBACK_0 = (MethodInterceptor)var10001[0];
        }

    }

    static void CGLIB$STATICHOOK1() {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        Class var0 = Class.forName("com.roboslyq.learn.aop.UserDaoNoInterface$$EnhancerBySpringCGLIB$$ee13cb35");
        Class var1;
        Method[] var10000 = ReflectUtils.findMethods(new String[]{"equals", "(Ljava/lang/Object;)Z", "toString", "()Ljava/lang/String;", "hashCode", "()I", "clone", "()Ljava/lang/Object;"}, (var1 = Class.forName("java.lang.Object")).getDeclaredMethods());
        CGLIB$equals$2$Method = var10000[0];
        CGLIB$equals$2$Proxy = MethodProxy.create(var1, var0, "(Ljava/lang/Object;)Z", "equals", "CGLIB$equals$2");
        CGLIB$toString$3$Method = var10000[1];
        CGLIB$toString$3$Proxy = MethodProxy.create(var1, var0, "()Ljava/lang/String;", "toString", "CGLIB$toString$3");
        CGLIB$hashCode$4$Method = var10000[2];
        CGLIB$hashCode$4$Proxy = MethodProxy.create(var1, var0, "()I", "hashCode", "CGLIB$hashCode$4");
        CGLIB$clone$5$Method = var10000[3];
        CGLIB$clone$5$Proxy = MethodProxy.create(var1, var0, "()Ljava/lang/Object;", "clone", "CGLIB$clone$5");
        var10000 = ReflectUtils.findMethods(new String[]{"deleteUser", "()V", "addUser", "()V"}, (var1 = Class.forName("com.roboslyq.learn.aop.UserDaoNoInterface")).getDeclaredMethods());
        CGLIB$deleteUser$0$Method = var10000[0];
        CGLIB$deleteUser$0$Proxy = MethodProxy.create(var1, var0, "()V", "deleteUser", "CGLIB$deleteUser$0");
        CGLIB$addUser$1$Method = var10000[1];
        CGLIB$addUser$1$Proxy = MethodProxy.create(var1, var0, "()V", "addUser", "CGLIB$addUser$1");
    }

    static void CGLIB$STATICHOOK2() {
        try {
            ;
        } catch (Error | RuntimeException var0) {
            throw var0;
        } catch (Throwable var1) {
            throw new UndeclaredThrowableException(var1);
        }
    }

    public final void addAdvice(Advice var1) throws AopConfigException {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).addAdvice(var1);
        } catch (Error | AopConfigException | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void addAdvice(int var1, Advice var2) throws AopConfigException {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).addAdvice(var1, var2);
        } catch (Error | AopConfigException | RuntimeException var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final boolean isPreFiltered() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).isPreFiltered();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final void addAdvisor(int var1, Advisor var2) throws AopConfigException {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).addAdvisor(var1, var2);
        } catch (Error | AopConfigException | RuntimeException var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void addAdvisor(Advisor var1) throws AopConfigException {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).addAdvisor(var1);
        } catch (Error | AopConfigException | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean removeAdvice(Advice var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).removeAdvice(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean removeAdvisor(Advisor var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).removeAdvisor(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void removeAdvisor(int var1) throws AopConfigException {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            ((Advised)var10000.loadObject()).removeAdvisor(var1);
        } catch (Error | AopConfigException | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean replaceAdvisor(Advisor var1, Advisor var2) throws AopConfigException {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).replaceAdvisor(var1, var2);
        } catch (Error | AopConfigException | RuntimeException var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final String toProxyConfigString() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).toProxyConfigString();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final Class[] getProxiedInterfaces() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).getProxiedInterfaces();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }

    public final boolean isInterfaceProxied(Class var1) {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((Advised)var10000.loadObject()).isInterfaceProxied(var1);
        } catch (Error | RuntimeException var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public static MethodProxy CGLIB$findMethodProxy(Signature var0) {
        String var10000 = var0.toString();
        switch(var10000.hashCode()) {
        case -508378822:
            if (var10000.equals("clone()Ljava/lang/Object;")) {
                return CGLIB$clone$5$Proxy;
            }
            break;
        case -351960161:
            if (var10000.equals("deleteUser()V")) {
                return CGLIB$deleteUser$0$Proxy;
            }
            break;
        case 1761046505:
            if (var10000.equals("addUser()V")) {
                return CGLIB$addUser$1$Proxy;
            }
            break;
        case 1826985398:
            if (var10000.equals("equals(Ljava/lang/Object;)Z")) {
                return CGLIB$equals$2$Proxy;
            }
            break;
        case 1913648695:
            if (var10000.equals("toString()Ljava/lang/String;")) {
                return CGLIB$toString$3$Proxy;
            }
            break;
        case 1984935277:
            if (var10000.equals("hashCode()I")) {
                return CGLIB$hashCode$4$Proxy;
            }
        }

        return null;
    }

    public final Class getTargetClass() {
        try {
            Dispatcher var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            return ((TargetClassAware)var10000.loadObject()).getTargetClass();
        } catch (Error | RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }
}
```

# 八、参考资料

https://www.cnblogs.com/51life/p/9482734.html
