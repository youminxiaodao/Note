## BeanFactory和ApplicationContext的区别

本质区别:使用BeanFactory的bean是延迟加载的，ApplicationContext是非延时加载的

## Spring中的bean

不指定scope下，都是单例bean，且是饿汉式加载(容器启动实例就创建好)；指定scope为prototype是多例，是懒汉式加载(IOC容器启动的时候，不会创建对象，而是在第一次使用的时候才创建)



## BeanPostProcessor的作用、生效过程

## BeanFactoryPostProcessor



## xxxAware接口的作用、加载过程



## Spring切换环境的方法

1.运行时增加JVM参数来切换：-Dspring.profiles.active=xxx

2.通过代码来激活:ctx.getEnviroment.setActiveProfile("xxx");



## 切面类与普通类实例化顺序

切面类初始为单例加载进IOC容器，比普通类快

## @Import注入之ImportSelector和ImportBeanDefinitionRegistrar的区别

### ImportSelector

String[] selectImports(AnnotationMetadata importingClassMetadata);

需要有一个返回值

```java
public class MyImportSelector implements ImportSelector {
   @Override
   public String[] selectImports(AnnotationMetadata importingClassMetadata) {
      return new String[aaa.class.getName()];
   }
}
```

### ImportBeanDefinitionRegistrar

public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

更小的细粒度，可以定制BeanDefinition

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
   @Override
   public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
      boolean aaa = registry.containsBeanDefinition(aaa.class.getName());
      boolean bbb = registry.containsBeanDefinition(bbb.class.getName());
      if (aaa && bbb) {
         RootBeanDefinition aBeanDefinition = new RootBeanDefinition(aaa.class.getName());
         RootBeanDefinition bBeanDefinition = new RootBeanDefinition(bbb.class.getName());
         registry.removeBeanDefinition(aaa.class.getName(),aBeanDefinition);
         registry.removeBeanDefinition(bbb.class.getName(),aBeanDefinition);
      }
   }
}
```

