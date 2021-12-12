#  Spring之IOC

![Spring IOC](04-Spring%E4%B9%8BIOC/Spring%20IOC.png)

## 1.1. Introduction to the Spring IoC Container and Beans

> Inversion of Control (IoC) is also known as dependency injection (DI). It is a process whereby objects define their dependencies (that is, the other objects they work with) only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method. The container then injects those dependencies when it creates the bean. This process is fundamentally the inverse (hence the name, Inversion of Control) of the bean itself controlling the instantiation or location of its dependencies by using direct construction of classes or a mechanism such as the Service Locator pattern.

上面这一段是Spring官网的定义，翻译过来有以下以下关键点：

- 控制反转(IOC)即依赖注入(DI），我们可以认为这两者是同一个概念。本质是我们通过依赖注入可以实现控制反转。
- 对象可以通过以下三种方式注入他们的依赖
  - constructor arguments：构造函数参数(建议使用)
  - factory method:工厂方法
  - set方法注入

`org.springframework.beans` and `org.springframework.context` 是Spring IOC最基础的包。其核心的概念就是BeanFacory和ApplicationContext。

BeanFactory接口提供了一种高级配置机制，能够管理任何类型的对象，此被管理的对象在IOC中叫做Bean。在Spring框架中，万物皆可Bean。其中BeanFactory提了基础的Bean管理功能，比如初始化，Bean查询等。而ApplicationContext是BeanFactory的子接口，提供了以下扩展:

- Easier integration with Spring’s AOP features：容易与AOP集成
- Message resource handling (for use in internationalization)：国际化支持
- Event publication:事件发布
- Application-layer specific contexts such as the `WebApplicationContext` for use in web applications.：应用层的特殊Context,例如和Web相关的WebApplicationContext。

## 1.2. Container Overview

> The `org.springframework.context.ApplicationContext` interface represents the Spring IoC container and is responsible for instantiating, configuring, and assembling the beans. The container gets its instructions on what objects to instantiate, configure, and assemble by reading configuration metadata. The configuration metadata is represented in XML, Java annotations, or Java code. It lets you express the objects that compose your application and the rich interdependencies between those objects.

Spring中的容器，最核心的类就是ApplicationContext，这个类就代理了SpringFramework的容器。主要职能如下：

- 实现化Bean
- 配置Bean
- 组装Bean
- 查找Bean

其中，Bean的配置元信息可以有以下三种方式

- XML
- Java Annotaion
- Java 硬编码
- Property（比较少见，但Property可以描述Bean）

BeanFactory继承体系如下：

![image-20211211231804285](04-Spring%E4%B9%8BIOC%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/image-20211211231804285.png)

ApplicationContext继承体系如下：

![image-20211211231923714](04-Spring%E4%B9%8BIOC%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/image-20211211231923714.png)

所以，ApplicationContext也继承了Bean体系。但是具体实现相关的功能不是直接实现的，而是通过组给BeanFactory继承体系中的`DefaultListableBeanFacotry`这个类。所以基本可以认为ApplicationContext中的主要干活的类就是`DefaultListableBeanFacotry`。

高层视力理解Spring是怎么工作的：

> Your application classes are combined with configuration metadata so that, after the `ApplicationContext` is created and initialized, you have a fully configured and executable system or application.

![container magic](04-Spring%E4%B9%8BIOC%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/container-magic.png)

翻译过来就是：应用中的classes是通过配置元信息(xml,annotion,java code等)组装起来的。然后ApplicationContext被创建和初始化。

###  1.2.1. Configuration Metadata

####  XML-based configuration metadata

示例配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="..." class="...">  
        <!-- collaborators and configuration for this bean go here -->
    </bean>

    <bean id="..." class="...">
        <!-- collaborators and configuration for this bean go here -->
    </bean>

    <!-- more bean definitions go here -->

</beans>
```

> id:唯一标识一个Bean
>
>  `class` :Bean对应的Class类全名称

####  [Annotation-based configuration](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-annotation-config)

> - Spring 2.5 introduced support for annotation-based configuration metadata.

#### [Java-based configuration](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-java)

-  [`@Configuration`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html)
- [`@Bean`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Bean.html)
- [`@Import`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Import.html)
-  [`@DependsOn`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/DependsOn.html) 

###  1.2.2. Instantiating a Container

#### 实例化容器

```java
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
```

`services.xml` configuration file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- services -->

    <bean id="petStore" class="org.springframework.samples.jpetstore.services.PetStoreServiceImpl">
        <property name="accountDao" ref="accountDao"/>
        <property name="itemDao" ref="itemDao"/>
        <!-- additional collaborators and configuration for this bean go here -->
    </bean>

    <!-- more bean definitions for services go here -->

</beans>
```

`daos.xml` file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="accountDao"
        class="org.springframework.samples.jpetstore.dao.jpa.JpaAccountDao">
        <!-- additional collaborators and configuration for this bean go here -->
    </bean>

    <bean id="itemDao" class="org.springframework.samples.jpetstore.dao.jpa.JpaItemDao">
        <!-- additional collaborators and configuration for this bean go here -->
    </bean>

    <!-- more bean definitions for data access objects go here -->

</beans>
```

#### Composing XML-based Configuration Metadata

将多个XMl合并导入：

```xml
<beans>
    <import resource="services.xml"/>
    <import resource="resources/messageSource.xml"/>
    <import resource="/resources/themeSource.xml"/>

    <bean id="bean1" class="..."/>
    <bean id="bean2" class="..."/>
</beans>
```

#### The Groovy Bean Definition DSL

通过Groovy来定义Bean

```java
beans {
    dataSource(BasicDataSource) {
        driverClassName = "org.hsqldb.jdbcDriver"
        url = "jdbc:hsqldb:mem:grailsDB"
        username = "sa"
        password = ""
        settings = [mynew:"setting"]
    }
    sessionFactory(SessionFactory) {
        dataSource = dataSource
    }
    myService(MyService) {
        nestedBean = { AnotherBean bean ->
            dataSource = dataSource
        }
    }
}
```

### 1.2.3. Using the Container

> 使用容器

```java
// create and configure beans:容器初始化
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

// retrieve configured instance:获取对应的Bean
PetStoreService service = context.getBean("petStore", PetStoreService.class);

// use configured instance ：使用对应的Bean
List<String> userList = service.getUsernameList();
```



## 1.3. Bean Overview

Spring IOC容器是管理着一个或者多个Bean，这些Bean是通过配置元信息创建的。这些配置元信息在SpringIOC中，是通过`BeanDefinition`这个类来定义的。

BeanDefintion主要定义以下四个方面的内容：

- A package-qualified class name: typically, the actual implementation class of the bean being defined.
  - 真实的Class原始对象
- Bean behavioral configuration elements, which state how the bean should behave in the container (scope, lifecycle callbacks, and so forth).
  - Bean在contain中的行为 ，例如scope,生命周期回调等
- References to other beans that are needed for the bean to do its work. These references are also called collaborators or dependencies.
  - Bean之前的依赖关系
- Other configuration settings to set in the newly created object — for example, the size limit of the pool or the number of connections to use in a bean hat manages a connection pool.
  - Bean自身的一些属性配置

**Bean定义如下：**

| Property                 | Explained in…                                                | 描述                                                         |
| :----------------------- | :----------------------------------------------------------- | ------------------------------------------------------------ |
| Class                    | [Instantiating Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-class) | Class属性用来实例化Bean                                      |
| Name                     | [Naming Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-beanname) | Name属性用来定义Bean的名称，IOC容器可能通过Name来查询对应的Bean |
| Scope                    | [Bean Scopes](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes) | Scope属性来描述Bean的作用范围，比如单例，原型，Request,Session等 |
| Constructor arguments    | [Dependency Injection](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-collaborators) | 依赖注入使用，描述了构造函数的相关参数，可以通过栣函数注入   |
| Properties               | [Dependency Injection](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-collaborators) | 依赖注入使用，可以通过属性配置来实现Setter注入               |
| Autowiring mode          | [Autowiring Collaborators](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-autowire) | 自动注入                                                     |
| Lazy initialization mode | [Lazy-initialized Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lazy-init) | 延迟初始化                                                   |
| Initialization method    | [Initialization Callbacks](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle-initializingbean) | 初始化方法，在Bean实例化后会被调用                           |
| Destruction method       | [Destruction Callbacks](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle-disposablebean) | 销毁方法                                                     |

- 注意：除了正常的元信息配置的受SpringIOC管理的Bean，也支持非Spring管理的Bean注入。主要是通过`DefaultListableBeanFactort` 和方法`registerSingleton(..)` and `registerBeanDefinition(..)`。

#### 1.3.1. Naming Beans

一个Bean的ID必须唯一，但一个Bean可以允许有很多不同的别名。在Xml配置中，通过id这个属性来配置Bean的唯一标识。别名使用name属性，如果有多个别名，使用逗号分割即可。比如name="beanA,beana,xxxB"

##### Aliasing a Bean outside the Bean Definition

```java
<alias name="fromName" alias="toName"/>
```

除在定义Bean时通过name属性指定Bean的别名，也可以在外部单独的通过alias来给Bean设置别名。

#### 1.3.2. Instantiating Beans

##### Instantiation with a Constructor

通过构造函数实例化一个Bean。最常见的配置

```xml
<bean id="exampleBean" class="examples.ExampleBean"/>

<bean name="anotherExample" class="examples.ExampleBeanTwo"/>
```



##### Instantiation with a Static Factory Method

通过静态工厂方法

```xml
<bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>
```

其中，`examples.ClientService`的实现如下：

```java
public class ClientService {
    private static ClientService clientService = new ClientService();
    private ClientService() {}

    public static ClientService createInstance() {
        return clientService;
    }
}
```



##### Instantiation by Using an Instance Factory Method

实例工厂方法

```xml
<!-- the factory bean, which contains a method called createInstance() -->
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
    <!-- inject any dependencies required by this locator bean -->
</bean>

<!-- the bean to be created via the factory bean -->
<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>
```

```java
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }
}
```

一个工厂Bean允许有多个工厂方法，即可以实例化多个不同的类。

```xml
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
    <!-- inject any dependencies required by this locator bean -->
</bean>

<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>

<bean id="accountService"
    factory-bean="serviceLocator"
    factory-method="createAccountServiceInstance"/>
```

##### Determining a Bean’s Runtime Type

> 运行时创建一个Bean

主要是通过FactoryBean(工厂Bean来实例化对应的Bean)

```java
public interface FactoryBean<T> {

	@Nullable
	T getObject() throws Exception;

	@Nullable
	Class<?> getObjectType();

	default boolean isSingleton() {
		return true;
	}

}
```

最关键的是getObject方法，返回了具体的Bean的实例。

## 1.4. Dependencies(Bean依赖)

#### 1.4.1. Dependency Injection

> 依赖注入

##### Constructor-based Dependency Injection

构造函数依赖注入

- 参数也是Bean,最常见的场景

  ```java
  package x.y;
  
  public class ThingOne {
  
      public ThingOne(ThingTwo thingTwo, ThingThree thingThree) {
          // ...
      }
  }
  ```

  xml配置

  ```xml
  <beans>
      <bean id="beanOne" class="x.y.ThingOne">
          <constructor-arg ref="beanTwo"/>
          <constructor-arg ref="beanThree"/>
      </bean>
  
      <bean id="beanTwo" class="x.y.ThingTwo"/>
  
      <bean id="beanThree" class="x.y.ThingThree"/>
  </beans>
  ```

- 参数是基本类型

  ```java
  package examples;
  
  public class ExampleBean {
  
      // Number of years to calculate the Ultimate Answer
      private final int years;
  
      // The Answer to Life, the Universe, and Everything
      private final String ultimateAnswer;
  
      public ExampleBean(int years, String ultimateAnswer) {
          this.years = years;
          this.ultimateAnswer = ultimateAnswer;
      }
  }
  ```

  xml配置

  ```xml
  <bean id="exampleBean" class="examples.ExampleBean">
      <constructor-arg type="int" value="7500000"/>
      <constructor-arg type="java.lang.String" value="42"/>
  </bean>
  ```

- Constructor argument index:指定索引

  ```XML
  <bean id="exampleBean" class="examples.ExampleBean">
      <constructor-arg index="0" value="7500000"/>
      <constructor-arg index="1" value="42"/>
  </bean>
  ```

- Constructor argument name: 指定参数名称

  ```java
  <bean id="exampleBean" class="examples.ExampleBean">
      <constructor-arg name="years" value="7500000"/>
      <constructor-arg name="ultimateAnswer" value="42"/>
  </bean>
  ```

  

##### Setter-based Dependency Injection

Setter方法注入

```java
public class SimpleMovieLister {

    // the SimpleMovieLister has a dependency on the MovieFinder
    private MovieFinder movieFinder;

    // a setter method so that the Spring container can inject a MovieFinder
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // business logic that actually uses the injected MovieFinder is omitted...
}
```



##### Dependency Resolution Process 依赖项解析过程

- ApplicationContext是用描述所有bean的配置元数据创建和初始化的。配置元数据可以由XML、Java代码或annaotation注解指定。

- 对于每个bean，其依赖关系都以属性(property,setter方法)、构造函数参数或静态工厂方法的参数的形式表示（如果您使用静态工厂方法而不是普通构造函数）。这些依赖关系在bean实际创建时提供给bean。

- 每个属性或构造函数参数都是要设置的值的实际定义，或者是对容器中另一个bean的引用。
- 作为值的每个属性或构造函数参数都将从其指定格式转换为该属性或构造函数参数的实际类型。默认情况下，Spring可以将以字符串格式提供的值转换为所有内置类型，例如int、long、string、boolean等

Circular dependencies

- 循环引用可以通过setter注入，而不是构造函数注入解决。

#### 1.4.2. Dependencies and Configuration in Detail

##### Straight Values(直接值)

> 原生数据类型或者String类型

```xml
<bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <!-- results in a setDriverClassName(String) call -->
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
    <property name="username" value="root"/>
    <property name="password" value="misterkaoli"/>
</bean>
```

也可以使用Property命令空间P:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close"
        p:driverClassName="com.mysql.jdbc.Driver"
        p:url="jdbc:mysql://localhost:3306/mydb"
        p:username="root"
        p:password="misterkaoli"/>

</beans>
```

也可以使用第三种方式配置：

```xml
<bean id="mappings"
    class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">

    <!-- typed as a java.util.Properties -->
    <property name="properties">
        <value>
            jdbc.driver.className=com.mysql.jdbc.Driver
            jdbc.url=jdbc:mysql://localhost:3306/mydb
        </value>
    </property>
</bean>
```

##### **The** `idref` **element**（id关联）

> ID引用

```xml
<bean id="theTargetBean" class="..."/>

<bean id="theClientBean" class="...">
    <property name="targetName">
        <idref bean="theTargetBean"/>
    </property>
</bean>
```

另一种配置

```xml
<bean id="theTargetBean" class="..." />

<bean id="client" class="...">
    <property name="targetName" value="theTargetBean"/>
</bean>
```

##### References to Other Beans (Collaborators)（其它Bean引用）

```xml
<!-- in the parent context -->
<bean id="accountService" class="com.something.SimpleAccountService">
    <!-- insert dependencies as required here -->
</bean>
<!-- in the child (descendant) context -->
<bean id="accountService" <!-- bean name is the same as the parent bean -->
    class="org.springframework.aop.framework.ProxyFactoryBean">
    <property name="target">
        <ref parent="accountService"/> <!-- notice how we refer to the parent bean -->
    </property>
    <!-- insert other configuration and dependencies as required here -->
</bean>
```

##### Inner Beans（内嵌Bean）

```xml
<bean id="outer" class="...">
    <!-- instead of using a reference to a target bean, simply define the target bean inline -->
    <property name="target">
        <bean class="com.example.Person"> <!-- this is the inner bean -->
            <property name="name" value="Fiona Apple"/>
            <property name="age" value="25"/>
        </bean>
    </property>
</bean>
```

可以在属性内部直接定义Bean，而不需要在外部单独定义Bean

#####  Collections(集合配置)

> 集合类型主要包括以下：<list/>`, `<set/>`, `<map/>`, and `<props/>

```xml
<bean id="moreComplexObject" class="example.ComplexObject">
    <!-- results in a setAdminEmails(java.util.Properties) call -->
    <property name="adminEmails">
        <props>
            <prop key="administrator">administrator@example.org</prop>
            <prop key="support">support@example.org</prop>
            <prop key="development">development@example.org</prop>
        </props>
    </property>
    <!-- results in a setSomeList(java.util.List) call -->
    <property name="someList">
        <list>
            <value>a list element followed by a reference</value>
            <ref bean="myDataSource" />
        </list>
    </property>
    <!-- results in a setSomeMap(java.util.Map) call -->
    <property name="someMap">
        <map>
            <entry key="an entry" value="just some string"/>
            <entry key="a ref" value-ref="myDataSource"/>
        </map>
    </property>
    <!-- results in a setSomeSet(java.util.Set) call -->
    <property name="someSet">
        <set>
            <value>just some string</value>
            <ref bean="myDataSource" />
        </set>
    </property>
</bean>
```

**Collection Merging**

集合合并,当存在父子关系时，相关的属性可以覆盖，不会报错。

```xml
<beans>
    <bean id="parent" abstract="true" class="example.ComplexObject">
        <property name="adminEmails">
            <props>
                <prop key="administrator">administrator@example.com</prop>
                <prop key="support">support@example.com</prop>
            </props>
        </property>
    </bean>
    <bean id="child" parent="parent">
        <property name="adminEmails">
            <!-- the merge is specified on the child collection definition -->
            <props merge="true">
                <prop key="sales">sales@example.com</prop>
                <prop key="support">support@example.co.uk</prop>
            </props>
        </property>
    </bean>
<beans>
```

**Strongly-typed collection**

强类型转换。依赖于Java5之后的泛型编程。

```java
public class SomeClass {

    private Map<String, Float> accounts;

    public void setAccounts(Map<String, Float> accounts) {
        this.accounts = accounts;
    }
}
```

XMl配置

```xml

<beans>
    <bean id="something" class="x.y.SomeClass">
        <property name="accounts">
            <map>
                <entry key="one" value="9.99"/>
                <entry key="two" value="2.75"/>
                <entry key="six" value="3.99"/>
            </map>
        </property>
    </bean>
</beans>
```

如上面的配置，Spring的类型转换(Spring’s type conversion)会将xml配置中的字符串值(`9.99`, `2.75`, and `3.99`)直接转换为Float。

##### Null and Empty String Values

```xml
<bean class="ExampleBean">
    <property name="email" value=""/>
</bean>
```

##### XML Shortcut with the p-namespace

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean name="john-classic" class="com.example.Person">
        <property name="name" value="John Doe"/>
        <property name="spouse" ref="jane"/>
    </bean>

    <bean name="john-modern"
        class="com.example.Person"
        p:name="John Doe"
        p:spouse-ref="jane"/>

    <bean name="jane" class="com.example.Person">
        <property name="name" value="Jane Doe"/>
    </bean>
</beans>
```

##### XML Shortcut with the c-namespace

C namespace主要是处理构造函数的相关参数，自从3.1版本开始。可以用来替代之后的`constructor-arg`标签。

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:c="http://www.springframework.org/schema/c"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="beanTwo" class="x.y.ThingTwo"/>
    <bean id="beanThree" class="x.y.ThingThree"/>

    <!-- traditional declaration with optional argument names -->
    <bean id="beanOne" class="x.y.ThingOne">
        <constructor-arg name="thingTwo" ref="beanTwo"/>
        <constructor-arg name="thingThree" ref="beanThree"/>
        <constructor-arg name="email" value="something@somewhere.com"/>
    </bean>

    <!-- c-namespace declaration with argument names -->
    <bean id="beanOne" class="x.y.ThingOne" c:thingTwo-ref="beanTwo"
        c:thingThree-ref="beanThree" c:email="something@somewhere.com"/>

</beans>
```

也可以通过索引配置

```xml
<!-- c-namespace index declaration -->
<bean id="beanOne" class="x.y.ThingOne" c:_0-ref="beanTwo" c:_1-ref="beanThree"
    c:_2="something@somewhere.com"/>
```

##### Compound Property Names（复合属性）

```xml
<bean id="something" class="things.ThingOne">
    <property name="fred.bob.sammy" value="123" />
</bean>
```

即`something`中有一个属性fred的对象，然后fred对象中有一个属性bob,然后bob对象中有一个属性叫sammy。我们可以通过"fred.bob.sammy" 这种形式直接给bob对象中的sammy属性赋值。但前提是fred和bob对象不能为空，否则会报NullPointerException异常。

#### 1.4.3. Using `depends-on`

```xml
<bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
    <property name="manager" ref="manager" />
</bean>

<bean id="manager" class="ManagerBean" />
<bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />
```

当某个Bean需要依赖于另外一个Bean时，使用此配置。要注意，ref是指Bean之间的直接依赖，其中的一个Bean是另一个Bean的属性。而有很多时候，两个Bean之间不是直接的依赖关系。

#### 1.4.4. Lazy-initialized Beans

延迟加载Bean，主要是属性lazyInit。当前此属性为true时，IOC容器启动时，不会初始化Bean，而是在使用时才会初始化。

```xml
<bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
<bean name="not.lazy" class="com.something.AnotherBean"/>
```

上面是针对单个Bean的，也可以针此属性进行全局配置

```xml
<beans default-lazy-init="true">
    <!-- no beans will be pre-instantiated... -->
</beans>
```

#### 1.4.5. Autowiring Collaborators

自动装配合作者（依赖Bean），通长默认是关闭的。Spring容器可以自动装配协作bean之间的关系。通过检查ApplicationContext的内容，可以让Spring为bean自动解析协作者（引用的其他bean）。自动装配具有以下优点：

- 自动装配可以显著减少指定属性或构造函数参数的需要。（本章其他部分讨论的其他机制（如bean模板）在这方面也很有价值。）
- 自动装配可以随着对象的发展而更新配置。例如，如果需要向类添加依赖项，则无需修改配置即可自动满足该依赖项。因此，在开发过程中，autowiring 尤其有用，而不必在代码库变得更稳定时切换到显式连接。

当使用基于XML的配置元数据（请参见依赖注入）时，可以使用`<bean/>`元素的`autowire`属性为bean定义指定autowire模式。自动装配功能有四种模式。您可以为每个bean指定自动装配，因此可以选择要自动装配的bean。下表介绍了四种自动装配模式:

| Mode          | Explanation                                                  |
| ------------- | ------------------------------------------------------------ |
| `no`          | (Default) No autowiring. Bean references must be defined by `ref` elements. Changing the default setting is not recommended for larger deployments, because specifying collaborators explicitly gives greater control and clarity. To some extent, it documents the structure of a system. |
|               | （默认）无自动装配。Bean引用必须由ref元素定义。对于较大的部署，不建议更改默认设置，因为显式指定协作者可以提供更好的控制和清晰度。在某种程度上，它记录了一个系统的结构。 |
| `byName`      | Autowiring by property name. Spring looks for a bean with the same name as the property that needs to be autowired. For example, if a bean definition is set to autowire by name and it contains a `master` property (that is, it has a `setMaster(..)` method), Spring looks for a bean definition named `master` and uses it to set the property. |
|               | 按特性名称自动装配。Spring寻找一个与需要自动装配的属性同名的bean。例如，如果一个bean定义被设置为autowire by name，并且它包含一个master属性（也就是说，它有一个setMaster（..）方法），那么Spring会查找名为master的bean定义并使用它来设置属性。 |
| `byType`      | Lets a property be autowired if exactly one bean of the property type exists in the container. If more than one exists, a fatal exception is thrown, which indicates that you may not use `byType` autowiring for that bean. If there are no matching beans, nothing happens (the property is not set). |
|               | 如果容器中恰好存在该属性类型的一个bean，则允许该属性自动实现。如果存在多个，就会抛出一个致命异常，这表明您不能对该bean使用byType自动装配。如果没有匹配的bean，则什么也不会发生(没有设置属性)。 |
| `constructor` | Analogous to `byType` but applies to constructor arguments. If there is not exactly one bean of the constructor argument type in the container, a fatal error is raised. |
|               | 类似于byType，但适用于构造函数参数。如果容器中没有构造函数参数类型的确切bean，就会引发致命错误。 |

#### 1.4.6. Method Injection

方法注入，当一个Singleton Bean A 依赖于一个Scope Bean B时，因为Singleton Bean A只有一次实例化的机会，因此一旦实例化，它所依赖的Scope Bean B没有办修改。一个解决的方案是通过 `ApplicationContextAware`接口，注入AppilcationContext,然后需要Scope Bean B 时，每次都通过ApplicationContext.getBean("B")从容器拿，这样每次就得到了不同的Bean B。

```java
// a class that uses a stateful Command-style class to perform some processing
package fiona.apple;

// Spring-API imports
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CommandManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public Object process(Map commandState) {
        // grab a new instance of the appropriate Command
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    protected Command createCommand() {
        // notice the Spring API dependency!
        return this.applicationContext.getBean("command", Command.class);
    }

    public void setApplicationContext(
            ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```



## 1.5. Bean Scopes(作用域)

| Scope                                                        |                          | Description                                                  |
| :----------------------------------------------------------- | ------------------------ | :----------------------------------------------------------- |
| [singleton](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes-singleton) | 单例模式                 | (Default) Scopes a single bean definition to a single object instance for each Spring IoC container. |
| [prototype](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes-prototype) | 原型模式                 | Scopes a single bean definition to any number of object instances. |
| [request](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes-request) | WEB应用中request请求范围 | Scopes a single bean definition to the lifecycle of a single HTTP request. That is, each HTTP request has its own instance of a bean created off the back of a single bean definition. Only valid in the context of a web-aware Spring `ApplicationContext`. |
| [session](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes-session) | WEB应用中，会话范围      | Scopes a single bean definition to the lifecycle of an HTTP `Session`. Only valid in the context of a web-aware Spring `ApplicationContext`. |
| [application](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes-application) | WEB应用中，应用范围      | Scopes a single bean definition to the lifecycle of a `ServletContext`. Only valid in the context of a web-aware Spring `ApplicationContext`. |
| [websocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp-websocket-scope) |                          | Scopes a single bean definition to the lifecycle of a `WebSocket`. Only valid in the context of a web-aware Spring `ApplicationContext`. |

### 1.5.1. The Singleton Scope

> 全局只有一个Bean，此Bean ID唯一，最常见的模式。通常默认都是Singleton Bean。

![singleton](04-Spring%E4%B9%8BIOC%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/singleton.png)

### 1.5.2. The Prototype Scope

> 每次都是一个新的Bean，所有的Bean依赖于是不同的具体实例。

如下图，左边三个Bean依赖于accountDao Bean。每个bean都是不同的accountDao服务。

![prototype](04-Spring%E4%B9%8BIOC%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90/prototype.png)

### 1.5.3. Request, Session, Application, and WebSocket Scopes

> The `request`, `session`, `application`, and `websocket` scopes are available only if you use a web-aware Spring `ApplicationContext` implementation (such as `XmlWebApplicationContext`). If you use these scopes with regular Spring IoC containers, such as the `ClassPathXmlApplicationContext`, an `IllegalStateException` that complains about an unknown bean scope is thrown.

 `request`, `session`, `application`, and `websocket`仅适用于Web应用。

传统的web.xml初始化方式

```xml
<web-app>
    ...
    <listener>
        <listener-class>
            org.springframework.web.context.request.RequestContextListener
        </listener-class>
    </listener>
    ...
</web-app>
```

```xml
<web-app>
    ...
    <filter>
        <filter-name>requestContextFilter</filter-name>
        <filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>requestContextFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    ...
</web-app>
```

#### Request scope

XML配置

```xml
<bean id="loginAction" class="com.something.LoginAction" scope="request"/>
```

注解配置

```java
@RequestScope
@Component
public class LoginAction {
    // ...
}
```

#### Session Scope

XML配置

```xml
<bean id="userPreferences" class="com.something.UserPreferences" scope="session"/>
```

注解配置

```java
@SessionScope
@Component
public class UserPreferences {
    // ...
}
```

#### Application Scope

XML配置

```xml
<bean id="userPreferences" class="com.something.UserPreferences" scope="session"/>
```

注解配置

```java
@SessionScope
@Component
public class UserPreferences {
    // ...
}
```

#### Scope Bean as a Dependency

XML配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- an HTTP Session-scoped bean exposed as a proxy -->
    <bean id="userPreferences" class="com.something.UserPreferences" scope="session">
        <!-- instructs the container to proxy the surrounding bean -->
        <aop:scoped-proxy/> 
    </bean>

    <!-- a singleton-scoped bean injected with a proxy to the above bean -->
    <bean id="userService" class="com.something.SimpleUserService">
        <!-- a reference to the proxied userPreferences bean -->
        <property name="userPreferences" ref="userPreferences"/>
    </bean>
</beans>
```

### 1.5.4 Custom Scopes(自定义作用域)

我们可以自定义作用域，也可以覆盖内建的作用域(除了singleton和prototype之外)。

## 1.6. Customizing the Nature of a Bean(自定义Bean的性质)

常见有以下三种方式：

- [Lifecycle Callbacks](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-lifecycle)
  - Bean的生命周期回调
- [`ApplicationContextAware` and `BeanNameAware`](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-aware)
  - ApplicationContext和BeanName注入接口
- [Other `Aware` Interfaces](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aware-list)
  - 其它Aware接口

### 1.6.1. Lifecycle Callbacks

#### Initialization Callbacks

我们可以通过实现`InitializingBean` 和`DisposableBean` 接口，容器会调用`afterPropertiesSet()` 和 `destroy()`回调方法。

>  JSR-250 `@PostConstruct` and `@PreDestroy` annotations are generally considered best practice for receiving lifecycle callbacks in a modern Spring application. Using these annotations means that your beans are not coupled to Spring-specific interfaces. For details, see [Using `@PostConstruct` and `@PreDestroy`](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-postconstruct-and-predestroy-annotations).

`@PostConstruct` and `@PreDestroy` 这两注解是JSR-250规范的(位于 javax.annotation.*包中)，不需要与SpringFrame进行绑定。

两个核心接口：

```java
public interface InitializingBean {
	void afterPropertiesSet() throws Exception;

}
```

```java
public interface DisposableBean {
    
    void destroy() throws Exception;
}
```

实现方式

- 实现InitializingBean接口

  ```java
  public class AnotherExampleBean implements InitializingBean {
  
      @Override
      public void afterPropertiesSet() {
          // do some initialization work
      }
  }
  ```

- 指定init-Method属性

  ```xml
  <bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
  ```

  ```java
  public class ExampleBean {
  
      public void init() {
          // do some initialization work
      }
  }
  ```

- 使用JSR-250注解`@PostConstruct`

  ```java
  public class ExampleBean {
  	@PostConstruct
      public void init() {
          // do some initialization work
      }
  }
  ```

- 实现`BeanPostProcessor`接口

  此方式是Spring自己的实现的方案。`BeanPostProcessor`在Bean的生命周期中十分重要。很多功能是基于此接口进行扩展实现。

#### Destruction Callbacks

实现方式

- 实现DisposableBean接口

  > 不建议，因为这样就与Spring进行了强绑定。

  ```xml
  <bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
  ```

  ```java
  public class AnotherExampleBean implements DisposableBean {
  
      @Override
      public void destroy() {
          // do some destruction work (like releasing pooled connections)
      }
  }
  ```

- 指定destroy-method属性

  ```xml
  <bean id="exampleInitBean" class="examples.ExampleBean" destroy-method="cleanup"/>
  ```

  ```java
  public class ExampleBean {
  
      public void cleanup() {
          // do some destruction work (like releasing pooled connections)
      }
  }
  ```

- 使用JSR-250注解`@PostConstruct`

  ```java
  public class ExampleBean {
  	@PostConstruct
      public void init() {
          // do some initialization work
      }
  }
  ```

- 实现`BeanPostProcessor`接口

  此方式是Spring自己的实现的方案。`BeanPostProcessor`在Bean的生命周期中十分重要。很多功能是基于此接口进行扩展实现。

#### Default Initialization and Destroy Methods（全局默认的）

```java
public class DefaultBlogService implements BlogService {

    private BlogDao blogDao;

    public void setBlogDao(BlogDao blogDao) {
        this.blogDao = blogDao;
    }

    // this is (unsurprisingly) the initialization callback method
    public void init() {
        if (this.blogDao == null) {
            throw new IllegalStateException("The [blogDao] property must be set.");
        }
    }
}
```

```xml
<beans default-init-method="init">

    <bean id="blogService" class="com.something.DefaultBlogService">
        <property name="blogDao" ref="blogDao" />
    </bean>

</beans>
```

将 default-init-method="init"属性放在根标签beans中，即可以实现所有的Bean默认的初始化方法。

#### Startup and Shutdown Callbacks

Lifecycle不是统一的Bean和Container的生命周期不一样，是根据自己的场景需要指定的生命周期。

> The Lifecycle interface defines the essential methods for any object that has its own lifecycle requirements (such as starting and stopping some background process):
>
> Lifecycle接口定义了具有自己生命周期需求的任何对象的基本方法（例如启动和停止某些后台进程）

```java
public interface Lifecycle {

    void start();

    void stop();

    boolean isRunning();
}
```

#### Shutting Down the Spring IoC Container Gracefully in Non-Web Applications（停机钩子函数）

```java
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class Boot {

    public static void main(final String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        // app runs here...

        // main method exits, hook is called prior to the app shutting down...
    }
}
```

### 1.6.2. `ApplicationContextAware` and `BeanNameAware`

```java
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
```

```java
public interface BeanNameAware {

    void setBeanName(String name) throws BeansException;
}
```



### 1.6.3. Other `Aware` Interfaces

| Name                             | Injected Dependency                                          | Explained in…                                                |
| :------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| `ApplicationContextAware`        | Declaring `ApplicationContext`.                              | [`ApplicationContextAware` and `BeanNameAware`](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-aware) |
| `ApplicationEventPublisherAware` | Event publisher of the enclosing `ApplicationContext`.       | [Additional Capabilities of the `ApplicationContext`](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-introduction) |
| `BeanClassLoaderAware`           | Class loader used to load the bean classes.                  | [Instantiating Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-class) |
| `BeanFactoryAware`               | Declaring `BeanFactory`.                                     | [The `BeanFactory`](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-beanfactory) |
| `BeanNameAware`                  | Name of the declaring bean.                                  | [`ApplicationContextAware` and `BeanNameAware`](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-aware) |
| `LoadTimeWeaverAware`            | Defined weaver for processing class definition at load time. | [Load-time Weaving with AspectJ in the Spring Framework](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop-aj-ltw) |
| `MessageSourceAware`             | Configured strategy for resolving messages (with support for parametrization and internationalization). | [Additional Capabilities of the `ApplicationContext`](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-introduction) |
| `NotificationPublisherAware`     | Spring JMX notification publisher.                           | [Notifications](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#jmx-notifications) |
| `ResourceLoaderAware`            | Configured loader for low-level access to resources.         | [Resources](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#resources) |
| `ServletConfigAware`             | Current `ServletConfig` the container runs in. Valid only in a web-aware Spring `ApplicationContext`. | [Spring MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc) |
| `ServletContextAware`            | Current `ServletContext` the container runs in. Valid only in a web-aware Spring `ApplicationContext`. | [Spring MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc) |

## 1.7. Bean Definition Inheritance（Bean定义继承）

> Bean允许有父子关系，Java使用继承来描述。而Bean的配置，则使用XMl来描述(如果使用xml进行Bean配置)。所以在处理Bean的继承的时候，需要spring 框架手动的去处理父子Bean的属性关系。

```java
<bean id="inheritedTestBean" abstract="true"
        class="org.springframework.beans.TestBean">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithDifferentClass"
        class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBean" init-method="initialize">  
    <property name="name" value="override"/>
    <!-- the age property value of 1 will be inherited from parent -->
</bean>
```



## 1.8. Container Extension Points(容器扩展点)

### 1.8.1. Customizing Beans by Using a `BeanPostProcessor`(控制Bean的)

### 1.8.2. Customizing Configuration Metadata with a `BeanFactoryPostProcessor`(通过BeanFactory层面控制配置元信息)

### 1.8.3. Customizing Instantiation Logic with a `FactoryBean`（通过工厂Bean控制实例化逻辑）



## 1.9. Annotation-based Container Configuration

注解配置Container

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

</beans>
```

上面的`<context:annotation-config/>`会注册下面相应的post-processors:

- [`ConfigurationClassPostProcessor`](https://docs.spring.io/spring-framework/docs/5.3.13/javadoc-api/org/springframework/context/annotation/ConfigurationClassPostProcessor.html)
- [`AutowiredAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/5.3.13/javadoc-api/org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.html)
- [`CommonAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/5.3.13/javadoc-api/org/springframework/context/annotation/CommonAnnotationBeanPostProcessor.html)
- [`PersistenceAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/5.3.13/javadoc-api/org/springframework/orm/jpa/support/PersistenceAnnotationBeanPostProcessor.html)
- [`EventListenerMethodProcessor`](https://docs.spring.io/spring-framework/docs/5.3.13/javadoc-api/org/springframework/context/event/EventListenerMethodProcessor.html)

> `<context:annotation-config/>` only looks for annotations on beans in the same application context in which it is defined. This means that, if you put `<context:annotation-config/>` in a `WebApplicationContext` for a `DispatcherServlet`, it only checks for `@Autowired` beans in your controllers, and not your services. See [The DispatcherServlet](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet) for more information.
>
> `<context:annotation-config/>`配置的作用范围是当前Application Context。因此，如果配置在WebApplicationContext中，仅仅会处理Controllers上的@Autowired,而不会处理具体services的相关依赖注入。因为services和Controllers在两个不同的ApplicationContext中。

### 1.9.1. @Required

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Required
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }

    // ...
}
```

- 在Bean配置时生效，会自动注入所依赖的类
- 如果所依赖的Bean在容器中没有，会抛出相应的异常。因此与构造函数注入类似，可以提前检查防止NullpointException。

>  The [`RequiredAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/5.3.13/javadoc-api/org/springframework/beans/factory/annotation/RequiredAnnotationBeanPostProcessor.html) must be registered as a bean to enable support for the `@Required` annotation.
>
>  [`RequiredAnnotationBeanPostProcessor`](https://docs.spring.io/spring-framework/docs/5.3.13/javadoc-api/org/springframework/beans/factory/annotation/RequiredAnnotationBeanPostProcessor.html) 处理器必须单独的作为一个Bean手动的注入到容器器，以便支持@Required注解。

>  The `@Required` annotation and `RequiredAnnotationBeanPostProcessor` are formally deprecated as of Spring Framework 5.1, in favor of using constructor injection for required settings (or a custom implementation of `InitializingBean.afterPropertiesSet()` or a custom `@PostConstruct` method along with bean property setter methods).
>
> @Required在Spring 5.1之后已经过时，不建议使用。推荐使用构造函数注入或者自定义实现`InitializingBean.afterPropertiesSet()` 或者使用注解@PostConstruct`

### 1.9.2. Using `@Autowired`

>  JSR 330’s `@Inject` annotation can be used in place of Spring’s `@Autowired` annotation

- 作用于构造函数上

  >  As of Spring Framework 4.3, an `@Autowired` annotation on such a constructor is no longer necessary if the target bean defines only one constructor to begin with. However, if several constructors are available and there is no primary/default constructor, at least one of the constructors must be annotated with `@Autowired` in order to instruct the container which one to use. See the discussion on [constructor resolution](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-autowired-annotation-constructor-resolution) for details.
  >
  > 基于Spring Framework 4.3版本之后，如果你的Bean只有一个构造函数，@Autowired不是必须的。如果你有多个构造函数，并且没有默认构造函数。那么必须手动使用@Autowired注解指定需要使用的构造函数。

  ```java
  public class MovieRecommender {
  
      private final CustomerPreferenceDao customerPreferenceDao;
  
      @Autowired
      public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
          this.customerPreferenceDao = customerPreferenceDao;
      }
  
      // ...
  }
  ```

- 作用于Setter方法上

  ```java
  public class SimpleMovieLister {
  
      private MovieFinder movieFinder;
  
      @Autowired
      public void setMovieFinder(MovieFinder movieFinder) {
          this.movieFinder = movieFinder;
      }
  
      // ...
  }
  ```

- 作用于普通的方法上(支持多个参数)

  ```java
  public class MovieRecommender {
  
      private MovieCatalog movieCatalog;
  
      private CustomerPreferenceDao customerPreferenceDao;
  
      @Autowired
      public void prepare(MovieCatalog movieCatalog,
              CustomerPreferenceDao customerPreferenceDao) {
          this.movieCatalog = movieCatalog;
          this.customerPreferenceDao = customerPreferenceDao;
      }
  
      // ...
  }
  ```

- 作用于field上(最常用的)

  > 作用于field与作用于构造函数中的可以混合使用，不会有冲突。

  ```java
  public class MovieRecommender {
  
      private final CustomerPreferenceDao customerPreferenceDao;
  
      @Autowired
      private MovieCatalog movieCatalog;
  
      @Autowired
      public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
          this.customerPreferenceDao = customerPreferenceDao;
      }
  
      // ...
  }
  
  ```

- 依赖注入一种类型的所有Bean

  ```java
  public class MovieRecommender {
  
      @Autowired
      private MovieCatalog[] movieCatalogs;
  
      // ...
  }
  ```

  ```java
  public class MovieRecommender {
  
      private Set<MovieCatalog> movieCatalogs;
  
      @Autowired
      public void setMovieCatalogs(Set<MovieCatalog> movieCatalogs) {
          this.movieCatalogs = movieCatalogs;
      }
  
      // ...
  }
  ```

  ```java
  public class MovieRecommender {
  
      private Map<String, MovieCatalog> movieCatalogs;
  
      @Autowired
      public void setMovieCatalogs(Map<String, MovieCatalog> movieCatalogs) {
          this.movieCatalogs = movieCatalogs;
      }
  
      // ...
  }
  ```

- 设置非必须

  ```java
  public class SimpleMovieLister {
  
      private MovieFinder movieFinder;
  
      @Autowired(required = false)
      public void setMovieFinder(MovieFinder movieFinder) {
          this.movieFinder = movieFinder;
      }
  
      // ...
  }
  ```

  Java8中借助于Optional为实现空指针检查

  ```java
  public class SimpleMovieLister {
  
      @Autowired
      public void setMovieFinder(Optional<MovieFinder> movieFinder) {
          ...
      }
  }
  ```

  Spring5中借助于@Nullable注解

  ```java
  public class SimpleMovieLister {
  
      @Autowired
      public void setMovieFinder(@Nullable MovieFinder movieFinder) {
          ...
      }
  }
  ```

### 1.9.3. Fine-tuning Annotation-based Autowiring with `@Primary`

> 使用@Primary注解微调@Autowiring的注入行为

```java
@Configuration
public class MovieConfiguration {

    @Bean
    @Primary
    public MovieCatalog firstMovieCatalog() { ... }

    @Bean
    public MovieCatalog secondMovieCatalog() { ... }

    // ...
}
```

```java
public class MovieRecommender {

    @Autowired
    private MovieCatalog movieCatalog;

    // ...
}
```



当一种类型有多个实现Bean时，可以通过@Primary注解指定默认注入的Bean类型。例如上面的示例，MovieCatalog有两个Bean实例，而MovieRecommender注入是使用哪一个呢？如果没有@Primary注解，会报错，因为找到了多个Bean。如果有@Primary注解，则默认注入@Primary注解所标识的Bean。

### 1.9.4. Fine-tuning Annotation-based Autowiring with Qualifiers

> 使用@Qualifiers注解微调@Autowiring的注入行为

```java
public class MovieRecommender {

    @Autowired
    @Qualifier("main")
    private MovieCatalog movieCatalog;

    // ...
}
```

```java
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(@Qualifier("main") MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

@Qualifier配合@Autowired使用，精确指定需要注入的Bean的名称。

### 1.9.5. Using Generics as Autowiring Qualifiers(泛型注入)

```java
@Configuration
public class MyConfiguration {

    @Bean
    public StringStore stringStore() {
        return new StringStore();
    }

    @Bean
    public IntegerStore integerStore() {
        return new IntegerStore();
    }
}
```

```java
@Autowired
private Store<String> s1; // <String> qualifier, injects the stringStore bean

@Autowired
private Store<Integer> s2; // <Integer> qualifier, injects the integerStore bean
```

s1会注入StringStore，因为Store中的泛型是String类型

s2会注入IntegerStore，因为Store中的泛型是Integer类型

### 1.9.6. Using `CustomAutowireConfigurer`

TODO

### 1.9.7. Injection with `@Resource`

> JSR-250 `@Resource` annotation (`javax.annotation.Resource`) 。仅作用于Field。

```java
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Resource(name="myMovieFinder") 
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

### 1.9.8. Using `@Value`

> `@Value` is typically used to inject externalized properties:
>
> @Value注解典型的应用场景是注入**外部化属性**

```java
@Component
public class MovieRecommender {

    private final String catalog;

    public MovieRecommender(@Value("${catalog.name}") String catalog) {
        this.catalog = catalog;
    }
}
```

导入Properties配置：

```java
@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig { }
```

Property配置文件 

```properties
catalog.name=MovieCatalog
```

依赖的Spring工具PropertySourcesPlaceholderConfigurer

```java
@Configuration
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
```

### 1.9.9. Using `@PostConstruct` and `@PreDestroy`

> The `CommonAnnotationBeanPostProcessor` not only recognizes the `@Resource` annotation but also the JSR-250 lifecycle annotations: `javax.annotation.PostConstruct` and `javax.annotation.PreDestroy`. 

```java
public class CachingMovieLister {

    @PostConstruct
    public void populateMovieCache() {
        // populates the movie cache upon initialization...
    }

    @PreDestroy
    public void clearMovieCache() {
        // clears the movie cache upon destruction...
    }
}
```



## 1.10. Classpath Scanning and Managed Components

## 1.11. Using JSR 330 Standard Annotations

## 1.12. Java-based Container Configuration

## 1.13. Environment Abstraction

## 1.14. Registering a LoadTimeWeaver

## 1.15. Additional Capabilities of the ApplicationContext

## 1.16. The BeanFactory

