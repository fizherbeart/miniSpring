Spring的IoC容器分为两类：BeanFactory和ApplicationContext

- IoC容器 仅支持ApplicationContext 
- 配置方式	仅支持Annotation 
- 扫描方式	支持按包名扫描 
- Bean类型	仅支持Singleton 
- Bean工厂		仅支持@Bean注解 
- 定制Bean		支持BeanPostProcessor 
- 依赖注入		支持构造方法、Setter方法与字段