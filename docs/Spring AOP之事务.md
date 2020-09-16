# Spring AOP之事务

> 原理：
>
> (1) 事务本身的实现
>
> (2) AOP切面技术

因为事务是AOP技术最好的应用场景，所以spring aop很多东西与事务结合再一起。即，事务与AOP不分离

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

## 启用事务

注解`@EnableTransactionManagement`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {

	boolean proxyTargetClass() default false;

	AdviceMode mode() default AdviceMode.PROXY;

	int order() default Ordered.LOWEST_PRECEDENCE;

}

```

### JDK代理

- ## AutoProxyRegistrar.class

  - ```
    AutoProxyRegistrar
    ProxyTransactionManagementConfiguration
    AspectJJtaTransactionManagementConfiguration
    internalTransactionalEventListenerFactory
    ```

    - ```
      InfrastructureAdvisorAutoProxyCreator
      BeanFactoryAdvisorRetrievalHelper
      ```

      

- ## ProxyTransactionManagementConfiguration

　###  CGLIB代理

- ## AspectJJtaTransactionManagementConfiguration

- ## internalTransactionalEventListenerFactory

```
AnnotationTransactionAspect
JtaAnnotationTransactionAspect
```

### 与springBean生命周期集成

> 上述通过@EnableTransactionManager注解 ，实现了相关的Bean的初化，其中InstantiationAwareBeanPostProcessor有一个Bean实现了`BeanPostProcessor`接口，因此在相关Bean的实现例化之前和之后，分别会调用如下两个方法：postProcessBeforeInstantiation(实例化前 )和postProcessAfterInitialization初始化。进而对相关的Bean进行增强，实现AOP相关功能 。

事务增强器的实现：

> 在`ProxyTransactionManagementConfiguration`中，加载了三个Bean，BeanFactoryTransactionAttributeSourceAdvisor，TransactionAttributeSource，TransactionInterceptor。

```java
@Configuration
public class ProxyTransactionManagementConfiguration extends AbstractTransactionManagementConfiguration {
	
	@Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    // 增强器，Bean的生命周期中，AOP时会调用
	public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor() {
		BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
		advisor.setTransactionAttributeSource(transactionAttributeSource());
        // 对应的拦截器，即具体的事务实现
		advisor.setAdvice(transactionInterceptor());
        // 事务不可用
		if (this.enableTx != null) {
			advisor.setOrder(this.enableTx.<Integer>getNumber("order"));
		}
        // 返回advisor，Bean的生命周期中会扫描所有的advisor,选则合适的advisor进行增强
		return advisor;
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TransactionAttributeSource transactionAttributeSource() {
		return new AnnotationTransactionAttributeSource();
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TransactionInterceptor transactionInterceptor() {
		TransactionInterceptor interceptor = new TransactionInterceptor();
		interceptor.setTransactionAttributeSource(transactionAttributeSource());
		if (this.txManager != null) {
			interceptor.setTransactionManager(this.txManager);
		}
		return interceptor;
	}

}
```



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

# 生成目标类源码分析

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sun.proxy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.DecoratingProxy;
import org.springframework.tests.sample.beans.INestedTestBean;
import org.springframework.tests.sample.beans.IOther;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.IndexedTestBean;

public final class $Proxy24 extends Proxy implements BeanNameAware, BeanFactoryAware, ITestBean, IOther, Comparable, SpringProxy, Advised, DecoratingProxy {
    private static Method m1;
    private static Method m48;
    private static Method m15;
    private static Method m22;
    private static Method m40;
    private static Method m35;
    private static Method m8;
    private static Method m4;
    private static Method m12;
    private static Method m37;
    private static Method m42;
    private static Method m23;
    private static Method m5;
    private static Method m21;
    private static Method m52;
    private static Method m2;
    private static Method m11;
    private static Method m32;
    private static Method m34;
    private static Method m24;
    private static Method m29;
    private static Method m43;
    private static Method m50;
    private static Method m7;
    private static Method m17;
    private static Method m44;
    private static Method m53;
    private static Method m19;
    private static Method m27;
    private static Method m14;
    private static Method m33;
    private static Method m49;
    private static Method m39;
    private static Method m38;
    private static Method m0;
    private static Method m18;
    private static Method m26;
    private static Method m25;
    private static Method m51;
    private static Method m6;
    private static Method m3;
    private static Method m9;
    private static Method m20;
    private static Method m54;
    private static Method m28;
    private static Method m45;
    private static Method m55;
    private static Method m41;
    private static Method m13;
    private static Method m31;
    private static Method m10;
    private static Method m30;
    private static Method m36;
    private static Method m16;
    private static Method m47;
    private static Method m46;

    public $Proxy24(InvocationHandler var1) throws  {
        super(var1);
    }

    public final boolean equals(Object var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final boolean isExposeProxy() throws  {
        try {
            return (Boolean)super.h.invoke(this, m48, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final INestedTestBean getLawyer() throws  {
        try {
            return (INestedTestBean)super.h.invoke(this, m15, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setNestedIntArray(int[][] var1) throws  {
        try {
            super.h.invoke(this, m22, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void removeAdvisor(int var1) throws AopConfigException {
        try {
            super.h.invoke(this, m40, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Class[] getProxiedInterfaces() throws  {
        try {
            return (Class[])super.h.invoke(this, m35, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final ITestBean getSpouse() throws  {
        try {
            return (ITestBean)super.h.invoke(this, m8, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setBeanFactory(BeanFactory var1) throws BeansException {
        try {
            super.h.invoke(this, m4, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final int[] getSomeIntArray() throws  {
        try {
            return (int[])super.h.invoke(this, m12, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean isInterfaceProxied(Class var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m37, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final boolean removeAdvice(Advice var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m42, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Integer[][] getNestedIntegerArray() throws  {
        try {
            return (Integer[][])super.h.invoke(this, m23, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String getName() throws  {
        try {
            return (String)super.h.invoke(this, m5, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setNestedIntegerArray(Integer[][] var1) throws  {
        try {
            super.h.invoke(this, m21, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void setExposeProxy(boolean var1) throws  {
        try {
            super.h.invoke(this, m52, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final String toString() throws  {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setSpouse(ITestBean var1) throws  {
        try {
            super.h.invoke(this, m11, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final int indexOf(Advice var1) throws  {
        try {
            return (Integer)super.h.invoke(this, m32, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final boolean isFrozen() throws  {
        try {
            return (Boolean)super.h.invoke(this, m34, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int[][] getNestedIntArray() throws  {
        try {
            return (int[][])super.h.invoke(this, m24, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int getAge() throws  {
        try {
            return (Integer)super.h.invoke(this, m29, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean replaceAdvisor(Advisor var1, Advisor var2) throws AopConfigException {
        try {
            return (Boolean)super.h.invoke(this, m43, new Object[]{var1, var2});
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final void setPreFiltered(boolean var1) throws  {
        try {
            super.h.invoke(this, m50, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final String[] getStringArray() throws  {
        try {
            return (String[])super.h.invoke(this, m7, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void unreliableFileOperation() throws IOException {
        try {
            super.h.invoke(this, m17, (Object[])null);
        } catch (RuntimeException | IOException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void addAdvisor(Advisor var1) throws AopConfigException {
        try {
            super.h.invoke(this, m44, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final boolean isProxyTargetClass() throws  {
        try {
            return (Boolean)super.h.invoke(this, m53, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final Integer[] getSomeIntegerArray() throws  {
        try {
            return (Integer[])super.h.invoke(this, m19, (Object[])null);
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

    public final Object returnsThis() throws  {
        try {
            return (Object)super.h.invoke(this, m14, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int indexOf(Advisor var1) throws  {
        try {
            return (Integer)super.h.invoke(this, m33, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final TargetSource getTargetSource() throws  {
        try {
            return (TargetSource)super.h.invoke(this, m49, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void addAdvice(int var1, Advice var2) throws AopConfigException {
        try {
            super.h.invoke(this, m39, new Object[]{var1, var2});
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final void addAdvice(Advice var1) throws AopConfigException {
        try {
            super.h.invoke(this, m38, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final int hashCode() throws  {
        try {
            return (Integer)super.h.invoke(this, m0, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final IndexedTestBean getNestedIndexedBean() throws  {
        try {
            return (IndexedTestBean)super.h.invoke(this, m18, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void exceptional(Throwable var1) throws Throwable {
        super.h.invoke(this, m26, new Object[]{var1});
    }

    public final int haveBirthday() throws  {
        try {
            return (Integer)super.h.invoke(this, m25, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setTargetSource(TargetSource var1) throws  {
        try {
            super.h.invoke(this, m51, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void setName(String var1) throws  {
        try {
            super.h.invoke(this, m6, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void setBeanName(String var1) throws  {
        try {
            super.h.invoke(this, m3, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final ITestBean[] getSpouses() throws  {
        try {
            return (ITestBean[])super.h.invoke(this, m9, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setSomeIntegerArray(Integer[] var1) throws  {
        try {
            super.h.invoke(this, m20, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Class getTargetClass() throws  {
        try {
            return (Class)super.h.invoke(this, m54, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setAge(int var1) throws  {
        try {
            super.h.invoke(this, m28, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void addAdvisor(int var1, Advisor var2) throws AopConfigException {
        try {
            super.h.invoke(this, m45, new Object[]{var1, var2});
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final Class getDecoratedClass() throws  {
        try {
            return (Class)super.h.invoke(this, m55, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean removeAdvisor(Advisor var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m41, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void setSomeIntArray(int[] var1) throws  {
        try {
            super.h.invoke(this, m13, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final int compareTo(Object var1) throws  {
        try {
            return (Integer)super.h.invoke(this, m31, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void setStringArray(String[] var1) throws  {
        try {
            super.h.invoke(this, m10, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void absquatulate() throws  {
        try {
            super.h.invoke(this, m30, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String toProxyConfigString() throws  {
        try {
            return (String)super.h.invoke(this, m36, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final INestedTestBean getDoctor() throws  {
        try {
            return (INestedTestBean)super.h.invoke(this, m16, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final Advisor[] getAdvisors() throws  {
        try {
            return (Advisor[])super.h.invoke(this, m47, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean isPreFiltered() throws  {
        try {
            return (Boolean)super.h.invoke(this, m46, (Object[])null);
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

其中有一个关键的接口Proxy，此类中有一个InvocationHandler类，如果是Jdk动态代理，则是JdkDynamicAopProxy。

所以，所有方法的入口在JdkDynamicAopProxy中的invoke相关方法。

其中，invoke内部有一个方法List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);，获取所有拦截器(增强器)。

通过集合list + 一个计数器，实现了拦截器链模式。

具体方法是将相关信息包装到MethodInvocation中：ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);

然后，调用invocation.proceed();

因为chain 被封装在Invocation中的this.interceptorsAndDynamicMethodMatchers 属性中，因此通过计数器，就能实现拦截器链调用模式：

```java
@Override
	@Nullable
	public Object proceed() throws Throwable {
		//	We start with an index of -1 and increment early.
		if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
			return invokeJoinpoint();
		}

		Object interceptorOrInterceptionAdvice =
				this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
		if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
			// Evaluate dynamic method matcher here: static part will already have
			// been evaluated and found to match.
			InterceptorAndDynamicMethodMatcher dm =
					(InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
			if (dm.methodMatcher.matches(this.method, this.targetClass, this.arguments)) {
				return dm.interceptor.invoke(this);
			}
			else {
				// Dynamic matching failed.
				// Skip this interceptor and invoke the next in the chain.
				return proceed();
			}
		}
		else {
			// It's an interceptor, so we just invoke it: The pointcut will have
			// been evaluated statically before this object was constructed.
            // 拦截器链，继承一下个拦截器调用
			return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
		}
	}

```



# 参考资料

https://www.cnblogs.com/dennyzhangdd/p/9602673.html

https://www.cnblogs.com/dennyzhangdd/p/9602673.html