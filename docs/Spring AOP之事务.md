# Spring AOP之事务

> 原理：
>
> (1) 事务本身的实现
>
> (2) AOP切面技术

## 事务本身实现

- ## **TransactionAspectSupport**

Spring事务采用AOP的方式实现，我们从- - TransactionAspectSupport这个类开始f分析。

1. 获取事务的属性（@Transactional注解中的配置）
2. 加载配置中的TransactionManager.
3. 获取收集事务信息TransactionInfo
4. 执行目标方法
5. 出现异常，尝试处理。
6. 清理事务相关信息
7. 提交事务

- ## PlatformTransactionManager

- ## TransactionStatus

- ## TransactionDefinition

  -  事务定义信息(事务隔离级别、传播行为、超时、只读、回滚规则)

## 编码式事务

### 基于底层 API 的编程式事务管理

> PlatformTransactionManager、TransactionDefinition 和 TransactionStatus 三个核心接口

### 基于 TransactionTemplate 的编程式事务管理

> TransactionTemplate



## 声明式事务



> 原理 是AOP切面增强，集成了Spring的事务实现。虽然下面共列举了四种声明式事务管理方式，但是这样的划分只是为了便于理解，其实后台的实现方式是一样的，只是用户使用的方式不同而已。

### 基于 TransactionInter… 的声明式事务管理

### 基于 TransactionProxy… 的声明式事务管理

> 前面的声明式事务虽然好，但是却存在一个非常恼人的问题：配置文件太多。我们必须针对每一个目标对象配置一个 ProxyFactoryBean；另外，虽然可以通过父子 Bean 的方式来复用 TransactionInterceptor 的配置，但是实际的复用几率也不高；这样，加上目标对象本身，每一个业务类可能需要对应三个 `<bean/>` 配置，随着业务类的增多，配置文件将会变得越来越庞大，管理配置文件又成了问题。
>
> 为了缓解这个问题，Spring 为我们提供了 TransactionProxyFactoryBean，用于将TransactionInterceptor 和 ProxyFactoryBean 的配置合二为一。如清单9所示：

### 基于 `<tx>` 命名空间的声明式事务管理

### 基于 @Transactional 的声明式事务管理



# 参考资料

https://www.cnblogs.com/dennyzhangdd/p/9602673.html