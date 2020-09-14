# Spring Aop原理 

> 在bean实例化完成之前和完成之后分别会自动BeanPostProcessor接口的postProcessBeforeInitialization和postProcessAfterInitialization方法

# AOP核心类

##  advisorCreator

继承 spring ioc的扩展接口 beanPostProcessor，主要用来扫描获取 advisor。

> beanPostProcessor: Spring容器中完成bean实例化、配置以及其他初始化方法前后要添加一些自己逻辑处理。  我们需要定义一个或多个BeanPostProcessor接口实现类，然后注册到Spring IoC容器中

AbstractAutoProxyCreator：Spring 为Spring AOP 模块暴露的可扩展抽象类，也是 AOP 中最核心的抽象类。Nepxion Matrix 框架便是基于此类对AOP进行扩展和增强。

BeanNameAutoProxyCreator：根据指定名称创建代理对象（阿里大名鼎鼎的连接池框架druid也基于此类做了扩展）。通过设置 advisor，可以对指定的 beanName 进行代理。支持模糊匹配。

AbstractAdvisorAutoProxyCreator：功能比较强大，默认扫描所有Advisor的实现类。相对于根据Bean名称匹配，该类更加灵活。动态的匹配每一个类，判断是否可以被代理，并寻找合适的增强类，以及生成代理类。

DefaultAdvisorAutoProxyCreator：AbstractAdvisorAutoProxyCreator的默认实现类。可以单独使用，在框架中使用AOP，尽量不要手动创建此对象。

AspectJAwareAdvisorAutoProxyCreator：Aspectj的实现方式，也是Spring Aop中最常用的实现方式，如果用注解方式，则用其子类AnnotationAwareAspectJAutoProxyCreator。

AnnotationAwareAspectJAutoProxyCreator：目前最常用的AOP使用方式。spring aop 开启注解方式之后，该类会扫描所有@Aspect()注释的类，生成对应的advisor。目前SpringBoot框架中默认支持的方式，自动配置。

##  advisor

顾问的意思，封装了spring aop中的切点和通知。 就是我们常用的@Aspect 注解标记得类。

##  advice

通知，也就是aop中增强的具体方法。