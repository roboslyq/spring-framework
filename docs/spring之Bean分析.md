# Spring之Bean分析

# BeanDefinition

| Property                 | Explained in…                                                |                                               |
| :----------------------- | :----------------------------------------------------------- | --------------------------------------------- |
| Class                    | [Instantiating Beans](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-class) | Bean 全类名，必须是具体类，不能用抽象类或接口 |
| Name                     | [Naming Beans](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-beanname) | Bean 的名称或者 ID                            |
| Scope                    | [Bean Scopes](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-scopes) | Bean 的作用域（如：singleton、prototype 等）  |
| Constructor arguments    | [Dependency Injection](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-collaborators) | Bean 构造器参数（用于依赖注入）               |
| Properties               | [Dependency Injection](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-collaborators) | Bean 属性设置（用于依赖注入）                 |
| Autowiring mode          | [Autowiring Collaborators](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-autowire) | Bean 自动绑定模式（如：通过名称 byName）      |
| Lazy initialization mode | [Lazy-initialized Beans](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-lazy-init) | Bean 延迟初始化模式（延迟和非延迟）           |
| Initialization method    | [Initialization Callbacks](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-initializingbean) | Bean 初始化回调方法名称                       |
| Destruction method       | [Destruction Callbacks](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-disposablebean) | Bean 销毁回调方法名称                         |

# Bean名称(Naming Beans)

- BeanNameGenerator

![DefaultBeanNameGenerator](images/spring之Bean分析/DefaultBeanNameGenerator.png)

Bean别名



# Bean初始化方式

## Constructor

> [Instantiation with a Constructor](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-class-ctor)

## Static method factory

> [Instantiation with a Static Factory Method](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-class-static-factory-method)

## Instance factory

> [Instantiation by Using an Instance Factory Method](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-class-instance-factory-method)

# Bean依赖(Dependencies)

- Constructor-based Dependency Injection
- Setter-based Dependency Injection

# Bean作用域(Scopes)

# 自定义Bean属性

- [Lifecycle Callbacks](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle)
- [`ApplicationContextAware` and `BeanNameAware`](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-aware)
- [Other `Aware` Interfaces](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#aware-list)

# BeanDefinition继承

