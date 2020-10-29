# Spring常用源文件清单

## 1、spring-core

### Class文件访问器

org/springframework/asm/ClassReader.java
org/springframework/asm/ClassVisitor.java
org/springframework/core/AttributeAccessor.java

### ENV抽象

org/springframework/core/env/ConfigurablePropertyResolver.java
org/springframework/core/env/ConfigurableEnvironment.java
org/springframework/core/env/Environment.java
org/springframework/core/env/StandardEnvironment.java

### Resource抽象

org/springframework/core/env/AbstractPropertyResolver.java
org/springframework/core/env/CompositePropertySource.java
org/springframework/core/env/MutablePropertySources.java
org/springframework/core/env/PropertyResolver.java
org/springframework/core/env/PropertySource.java

- org/springframework/core/env/PropertySourcesPropertyResolver.java
  - 解析Property配置文件

org/springframework/core/io/Resource.java
org/springframework/core/io/support/PathMatchingResourcePatternResolver.java
org/springframework/core/io/support/ResourcePropertySource.java
org/springframework/core/SimpleAliasRegistry.java

### spring.factories文件加载 

org/springframework/core/io/support/SpringFactoriesLoader.java

### class-meta抽象

org/springframework/core/type/classreading/AnnotationAttributesReadingVisitor.java
org/springframework/core/type/classreading/**AnnotationMetadataReadingVisitor.java**
org/springframework/core/type/classreading/CachingMetadataReaderFactory.java
org/springframework/core/type/classreading/MetadataReader.java

- org/springframework/core/type/classreading/SimpleMetadataReader.java
  - 注解扫描底层实现，通过此类读取class相关信息。

org/springframework/core/type/filter/AbstractTypeHierarchyTraversingFilter.java
org/springframework/core/type/filter/AnnotationTypeFilter.java

### Util工具包

org/springframework/util/ClassUtils.java
org/springframework/util/PropertyPlaceholderHelper.java
org/springframework/util/StringUtils.java

## 2、spring-beans

org/springframework/beans/BeanMetadataElement.java
org/springframework/beans/BeanUtils.java
org/springframework/beans/**BeanWrapper.java**
org/springframework/beans/factory/**BeanFactory.java**
org/springframework/beans/factory/BeanFactoryUtils.java
org/springframework/beans/factory/config/AutowireCapableBeanFactory.java
org/springframework/beans/factory/config/**BeanDefinition.java**
org/springframework/beans/factory/config/BeanDefinitionHolder.java
org/springframework/beans/factory/config/BeanDefinitionVisitor.java
org/springframework/beans/factory/config/**BeanFactoryPostProcessor.java**
org/springframework/beans/factory/config/ConfigurableBeanFactory.java
org/springframework/beans/factory/config/ConfigurableListableBeanFactory.java
org/springframework/beans/factory/config/**PlaceholderConfigurerSupport.java**
org/springframework/beans/factory/config/P**ropertyPlaceholderConfigurer.java**
org/springframework/beans/factory/config/PropertyResourceConfigurer.java
org/springframework/beans/factory/DisposableBean.java
org/springframework/beans/factory/HierarchicalBeanFactory.java
org/springframework/beans/factory/ListableBeanFactory.java
org/springframework/beans/factory/parsing/ComponentDefinition.java
org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.java
org/springframework/beans/factory/support/AbstractBeanDefinition.java
org/springframework/beans/factory/support/AbstractBeanDefinitionReader.java
org/springframework/beans/factory/support/AbstractBeanFactory.java
org/springframework/beans/factory/support/BeanDefinitionBuilder.java
org/springframework/beans/factory/support/BeanDefinitionReaderUtils.java
org/springframework/beans/factory/support/CglibSubclassingInstantiationStrategy.java
org/springframework/beans/factory/support/ChildBeanDefinition.java
org/springframework/beans/factory/support/ConstructorResolver.java
org/springframework/beans/factory/support/DefaultListableBeanFactory.java
org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java
org/springframework/beans/factory/support/FactoryBeanRegistrySupport.java
org/springframework/beans/factory/support/GenericBeanDefinition.java
org/springframework/beans/factory/support/RootBeanDefinition.java
org/springframework/beans/factory/support/SimpleInstantiationStrategy.java
org/springframework/beans/factory/xml/AbstractBeanDefinitionParser.java
org/springframework/beans/factory/xml/AbstractSingleBeanDefinitionParser.java
org/springframework/beans/factory/xml/BeanDefinitionParserDelegate.java
org/springframework/beans/factory/xml/DefaultBeanDefinitionDocumentReader.java
org/springframework/beans/factory/xml/DefaultDocumentLoader.java
org/springframework/beans/factory/xml/DefaultNamespaceHandlerResolver.java
org/springframework/beans/factory/xml/NamespaceHandlerSupport.java
org/springframework/beans/factory/xml/ParserContext.java

- org/springframework/beans/factory/xml/XmlBeanDefinitionReader.java
  - BeanFactory通过此类进行xml配置Bean加载

org/springframework/beans/factory/xml/XmlReaderContext.java

## 3、spring-context

### 注解处理

org/springframework/context/annotation/AdviceModeImportSelector.java
org/springframework/context/annotation/AnnotationConfigApplicationContext.java
org/springframework/context/annotation/AnnotationConfigApplicationContext.java
org/springframework/context/annotation/AutoProxyRegistrar.java

- org/springframework/context/annotation/ClassPathBeanDefinitionScanner.java
  - 扫描@ComponetScan的底层实现，被`ComponentScanAnnotationParser`调用。

org/springframework/context/annotation/ClassPathScanningCandidateComponentProvider.java
org/springframework/context/annotation/**ComponentScanAnnotationParser.java**
org/springframework/context/annotation/ComponentScanBeanDefinitionParser.java
org/springframework/context/annotation/ConfigurationClassBeanDefinitionReader.java
org/springframework/context/annotation/**ConfigurationClassParser.java**
org/springframework/context/annotation/**ConfigurationClassPostProcessor.java**
org/springframework/context/annotation/ImportSelector.java

调用过程：

BeanFactoryPostProcessor ->  ConfigurationClassPostProcessor ->  ConfigurationClassParser --> ComponentScanAnnotationParser



###　工具相关

org/springframework/context/config/AbstractPropertyLoadingBeanDefinitionParser.java
org/springframework/context/config/**ContextNamespaceHandler.java**
org/springframework/context/config/PropertyPlaceholderBeanDefinitionParser.java

- org/springframework/context/support/AbstractApplicationContext.java
  - refresh()方法所在

org/springframework/context/support/AbstractRefreshableApplicationContext.java
org/springframework/context/support/AbstractRefreshableConfigApplicationContext.java
org/springframework/context/support/AbstractXmlApplicationContext.java
org/springframework/context/support/ApplicationObjectSupport.java
org/springframework/context/support/ClassPathXmlApplicationContext.java
org/springframework/context/support/PostProcessorRegistrationDelegate.java
org/springframework/context/support/PropertySourcesPlaceholderConfigurer.java

## 4、spring-aop

org/springframework/aop/aspectj/annotation/AnnotationAwareAspectJAutoProxyCreator.java
org/springframework/aop/aspectj/autoproxy/AspectJAwareAdvisorAutoProxyCreator.java
org/springframework/aop/config/AopConfigUtils.java
org/springframework/aop/config/**AopNamespaceHandler.java**
org/springframework/aop/config/AopNamespaceUtils.java
org/springframework/aop/config/AspectJAutoProxyBeanDefinitionParser.java
org/springframework/aop/config/**ConfigBeanDefinitionParser.java**
org/springframework/aop/framework/autoproxy/AbstractAdvisorAutoProxyCreator.java
org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.java
org/springframework/aop/framework/autoproxy/InfrastructureAdvisorAutoProxyCreator.java
org/springframework/aop/framework/**CglibAopProxy.java**
org/springframework/aop/framework/DefaultAopProxyFactory.java
org/springframework/aop/framework/**JdkDynamicAopProxy.java**
org/springframework/aop/framework/ProxyCreatorSupport.java
org/springframework/aop/framework/ProxyFactory.java
org/springframework/aop/framework/ProxyFactoryBean.java
org/springframework/aop/support/AopUtils.java

## 5、spring-tx

org/springframework/transaction/annotation/ProxyTransactionManagementConfiguration.java
org/springframework/transaction/annotation/TransactionManagementConfigurationSelector.java
org/springframework/transaction/annotation/TransactionManagementConfigurationSelector.java
org/springframework/transaction/annotation/TransactionManagementConfigurationSelector.java
org/springframework/transaction/interceptor/TransactionAspectSupport.java
org/springframework/transaction/interceptor/TransactionAspectSupport.java
org/springframework/transaction/interceptor/TransactionInterceptor.java
org/springframework/transaction/support/CallbackPreferringPlatformTransactionManager.java
org/springframework/transaction/support/CallbackPreferringPlatformTransactionManager.java
org/springframework/transaction/support/TransactionOperations.java
org/springframework/transaction/support/TransactionTemplate.java
org/springframework/transaction/support/TransactionTemplate.java
org/springframework/transaction/TransactionDefinition.java
spring-tx/src/test/java/org/springframework/transaction/TxNamespaceHandlerTests.java

## 6、spring-web

org/springframework/http/client/reactive/ReactorClientHttpConnector.java
org/springframework/web/accept/ContentNegotiationManagerFactoryBean.java
org/springframework/web/accept/ContentNegotiationStrategy.java
org/springframework/web/client/RestTemplate.java
org/springframework/web/context/AbstractContextLoaderInitializer.java
org/springframework/web/context/ContextLoader.java
org/springframework/web/context/ContextLoader.java
org/springframework/web/context/ContextLoaderListener.java
org/springframework/web/context/support/WebApplicationContextUtils.java
org/springframework/web/context/support/WebApplicationObjectSupport.java
org/springframework/web/filter/OncePerRequestFilter.java
org/springframework/web/method/annotation/AbstractNamedValueMethodArgumentResolver.java
org/springframework/web/method/annotation/ModelAttributeMethodProcessor.java
org/springframework/web/method/annotation/RequestParamMethodArgumentResolver.java
org/springframework/web/method/HandlerMethod.java
org/springframework/web/method/support/HandlerMethodArgumentResolverComposite.java
org/springframework/web/method/support/InvocableHandlerMethod.java
org/springframework/web/util/DefaultUriBuilderFactory.java

## 7、spring-webflux

spring-webflux/src/main/java/org/springframework/web/reactive/DispatcherHandler.java
spring-webflux/src/main/java/org/springframework/web/reactive/HandlerAdapter.java
spring-webflux/src/main/java/org/springframework/web/reactive/HandlerMapping.java
spring-webflux/src/main/java/org/springframework/web/reactive/HandlerResult.java
spring-webflux/src/main/java/org/springframework/web/reactive/HandlerResultHandler.java

## 8、spring-webmvc

org/springframework/web/servlet/config/annotation/WebMvcConfigurationSupport.java
org/springframework/web/servlet/DispatcherServlet.java
org/springframework/web/servlet/FlashMapManager.java
org/springframework/web/servlet/FrameworkServlet.java
org/springframework/web/servlet/handler/AbstractDetectingUrlHandlerMapping.java
org/springframework/web/servlet/handler/AbstractHandlerMapping.java
org/springframework/web/servlet/handler/AbstractHandlerMethodMapping.java
org/springframework/web/servlet/handler/AbstractUrlHandlerMapping.java
org/springframework/web/servlet/HandlerAdapter.java
org/springframework/web/servlet/HandlerExecutionChain.java
org/springframework/web/servlet/HandlerMapping.java
org/springframework/web/servlet/HttpServletBean.java
org/springframework/web/servlet/LocaleResolver.java
org/springframework/web/servlet/mvc/method/AbstractHandlerMethodAdapter.java
org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodProcessor.java
org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.java
org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.java
org/springframework/web/servlet/mvc/method/annotation/ServletInvocableHandlerMethod.java
org/springframework/web/servlet/mvc/method/RequestMappingInfo.java
org/springframework/web/servlet/support/AbstractAnnotationConfigDispatcherServletInitializer.java
org/springframework/web/servlet/ViewResolver.java