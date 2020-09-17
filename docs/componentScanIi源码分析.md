1、spring boot是通过ConfigurationClassPostProcessor这个BeanFactoryPostProcessor类来处理。
2、AnnotationConfigApplicationContext入口类
3、ConfigurationClassPostProcessor


https://blog.csdn.net/mapleleafforest/article/details/86623578





# Spring 版本

AnnotationConfigApplicationContext#AnnotationConfigApplicationContext(String... basePackages)->

AnnotationConfigApplicationContext#scan(String... basePackages)->

ClassPathBeanDefinitionScanner#scan(String... basePackages) ->

ClassPathScanningCandidateComponentProvider#findCandidateComponents(String basePackage)->

ClassPathScanningCandidateComponentProvider#scanCandidateComponents(String basePackage)



# Spring Boot版本

AnnotationConfigServletWebServerApplicationContext#refresh()->

ServletWebServerApplicationContext#refresh() ->

AbstractApplicationContext#refresh()->

AbstractApplicationContext#invokeBeanFactoryPostProcessors(beanFactory)

AbstractApplicationContext#invokeBeanFactoryPostProcessors

PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors

ServletComponentRegisteringPostProcessor#postProcessBeanFactory->

ServletComponentRegisteringPostProcessor#scanPackage->

ClassPathBeanDefinitionScanner#scan(String... basePackages) ->

ClassPathScanningCandidateComponentProvider#findCandidateComponents(String basePackage)->

ClassPathScanningCandidateComponentProvider#scanCandidateComponents(String basePackage)