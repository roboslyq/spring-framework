# Spring Aop原理 

> 在bean实例化完成之前和完成之后分别会自动BeanPostProcessor接口的postProcessBeforeInitialization和postProcessAfterInitialization方法

# AOP核心类

##  advisorCreator

继承 spring ioc的扩展接口 beanPostProcessor，主要用来扫描获取 advisor。

> beanPostProcessor: Spring容器中完成bean实例化、配置以及其他初始化方法前后要添加一些自己逻辑处理。  我们需要定义一个或多个BeanPostProcessor接口实现类，然后注册到Spring IoC容器中

##  advisor

顾问的意思，封装了spring aop中的切点和通知。 就是我们常用的@Aspect 注解标记得类。

##  advice

通知，也就是aop中增强的具体方法。