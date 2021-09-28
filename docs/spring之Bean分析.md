# Spring之Bean分析

# BeanDefinition

| Property                 | Explained in…                                                |
| :----------------------- | :----------------------------------------------------------- |
| Class                    | [Instantiating Beans](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-class) |
| Name                     | [Naming Beans](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-beanname) |
| Scope                    | [Bean Scopes](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-scopes) |
| Constructor arguments    | [Dependency Injection](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-collaborators) |
| Properties               | [Dependency Injection](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-collaborators) |
| Autowiring mode          | [Autowiring Collaborators](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-autowire) |
| Lazy initialization mode | [Lazy-initialized Beans](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-lazy-init) |
| Initialization method    | [Initialization Callbacks](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-initializingbean) |
| Destruction method       | [Destruction Callbacks](https://docs.spring.io/spring-framework/docs/5.1.18.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-disposablebean) |

# Bean名称(Naming Beans)

- BeanNameGenerator

![DefaultBeanNameGenerator](images/spring之Bean分析/DefaultBeanNameGenerator.png)

## Bean别名



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

