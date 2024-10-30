# 手写spirng


## IoC容器的实现
## 实现ResourceResolver

为了扫描@ComponentScan注解的类所在的包下的所有类, 获取类名(xxx.yyy.zzz格式), 给classloader进行加载.
通过ResourceResolver获取指定类型的文件,并将路径转化为类名

使用方式:
```java
// 定义一个扫描器:
ResourceResolver rr = new ResourceResolver("org.example");
List<String> classList = rr.scan(res -> {
    String name = res.name(); // 资源名称"org/example/Hello.class"
    if (name.endsWith(".class")) { // 如果以.class结尾
        // 把"org/example/Hello.class"变为"org.example.Hello":
        return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
    }
    // 否则返回null表示不是有效的Class Name:
    return null;
});
```

实现思路
1. 先将包名转化basePath ("org.example" -> "org/example")
2. 使用ClassLoader的getResource方法获取basePath下的资源
3. 将资源路径转化为类名 (org/example/Hello.class -> org.example.Hello)
   1. 区分普通目录和jar包


问题
为什么使用只能使用classLoader的getResource方法, 而不能使用File的方法?
因为在jar包中, 文件是压缩的, 无法直接读取, 只能通过classLoader的getResource方法获取资源

为什么在classLoader里面会提供getResource方法?
因为在java中, 类加载器是用来加载类的, 而类是以文件的形式存在的, 所以类加载器也提供了获取资源的方法
为了不区分普通目录和jar包, 就封装了getResource方法

## 实现PropertyResolver

加载环境变量, 加载properties文件, 解析@Value中的信息

用法
```java
Properties props = new Properties();
// Java标准库读取properties文件
props.load(fileInput); // 文件输入流
PropertyResolver pr = new PropertyResolver(props);
// 后续代码调用...
getProperty("${app.title}");
getProperty("${app.title:Summer}");
getProperty("${app.title:123}", Integer.class);
```

实现思路
1. 构造函数
   1. 读取环境变量
   2. 传入的properties文件, 读取文件内容
2. getProperty方法解析@Value中的信息
   1. 


## 创建BeanDefinition

## 创建Bean实例