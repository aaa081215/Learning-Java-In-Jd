# Java类加载器ClassLoader 
> 本文带大家理解java类加载器的实现原理

## JAVA类装载方式
    1.隐式装载， 程序在运行过程中当碰到通过new 等方式生成对象时，隐式调用类装载器加载对应的类到jvm中。 
    2.显式装载， 通过class.forname()等方法，显式加载需要的类   
    先把保证程序运行的基础类一次性加载到jvm中，其它类等到jvm用到的时候再加载，这样的好处是节省了内存的开销，因为java最早就是为嵌入式系统而设计的，内存宝贵，这是一种可以理解的机制，而用到时再加载这也是java动态性的一种体现

## JAVA类装载器
![JAVA类装载器](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/TimLine图片20180806122600.jpg)

    1. Bootstrp loaderBootstrp加载器是用C++语言写的，它是在Java虚拟机启动后初始化的，它主要负责加载%JAVA_HOME%/jre/lib,-Xbootclasspath参数指定的路径以及%JAVA_HOME%/jre/classes中的
    2. ExtClassLoader  Bootstrp loader加载ExtClassLoader,并且将ExtClassLoader的父加载器设置为Bootstrp loader.ExtClassLoader是用Java写的，具体来说就是 sun.misc.Launcher$ExtClassLoader，ExtClassLoader主要加载%JAVA_HOME%/jre/lib/ext，此路径下的所有classes目录以及java.ext.dirs系统变量指定的路径中类库。类。
    3. AppClassLoader Bootstrp loader加载完ExtClassLoader后，就会加载AppClassLoader,并且将AppClassLoader的父加载器指定为 ExtClassLoader。AppClassLoader也是用Java写成的，它的实现类是 sun.misc.Launcher$AppClassLoader，另外我们知道ClassLoader中有个getSystemClassLoader方法,此方法返回的正是AppclassLoader.AppClassLoader主要负责加载classpath所指定的位置的类或者是jar文档，它也是Java程序默认的类加载器。

## JAVA类加载机制
    全盘负责：是指当一个ClassLoader装载一个类时，除非显示地使用另一个ClassLoader，则该类所依赖及引用的类也由这个CladdLoader载入。
    双亲委派：是指子类加载器如果没有加载过该目标类，就先委托父类加载器加载该目标类，只有在父类加载器找不到字节码文件的情况下才从自己的类路径中查找并装载目标类。

    1：避免重复加载      2：安全 
    (考虑到安全因素，我们试想一下，如果不使用这种委托模式，那我们就可以随时使用自定义的String来动态替代java核心api中定义类型，这样会存在非常大的安全隐患，而双亲委托的方式，就可以避免这种情况，因为String已经在启动时被加载，所以用户自定义类是无法加载一个自定义的ClassLoader。)

## JAVA类装载器如何协调工作

![JAVA类装载器如何协调工作](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/2.jpg)
![JAVA类装载器如何协调工作](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/3.jpg)
![JAVA类装载器如何协调工作](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/4.jpg)
![JAVA类装载器如何协调工作](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/5.jpg)

## Java中类的加载过程（如Dog类）：
    1.通过类型信息定位Dog.class文件。
    2.载入Dog.class文件，创建相应的Class对象。
    3.执行父类的静态字段定义时初始化语句和父类的静态初始化块。
    4.执行子类的静态字段定义时初始化语句和子类的静态初始化块。
    5.当使用new Dog()方式时，在堆上为Dog对象分配存储空间，并清零分配的存储空间。
    6.执行父类的字段定义时初始化语句和父类的构造函数。
    7.执行子类的字段定义时初始化语句和子类的构造函数。
    
## 定义自己的ClassLoader
    1.继承java.lang.ClassLoader
    2.重写父类的findClass方法
    3.一般不重写父类的loadClass方法 ??????
    
    (因为JDK已经在loadClass方法中帮我们实现了ClassLoader搜索类的算法，当在loadClass方法中搜索不到类时，loadClass方法就会调用findClass方法来搜索类，所以我们只需重写该方法即可。如没有特殊的要求，一般不建议重写loadClass搜索类的算法。)
    
## 定义自己的ClassLoader
    1.线程上下文类加载器  
        Java 提供了很多服务提供者接口（Service Provider Interface，SPI），允许第三方为这些接口提供实现。常见的 SPI 有 JDBC、JCE、JNDI、JAXP 和 JBI 等。
    2. Web容器类加载器  
        该类加载器也使用代理模式，所不同的是它是首先尝试去加载某个类，如果找不到再代理给父类加载器。这与一般类加载器的顺序是相反的。这是 Java Servlet 规范中的推荐做法，其目的是使得 Web 应用自己的类的优先级高于 Web 容器提供的类。
    3.OSGi类加载器  
        OSGi 中的每个模块都有对应的一个类加载器。它负责加载模块自己包含的 Java 包和类。当它需要加载 Java 核心库的类时（以 java开头的包和类），它会代理给父类加载器（通常是启动类加载器）来完成。当它需要加载所导入的 Java 类时，它会代理给导出此 Java 类的模块来完成加载。
        
## 常用ClassLoader
![JAVA类装载器如何协调工作](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/6.jpg)
![JAVA类装载器如何协调工作](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/7.jpg)
![JAVA类装载器如何协调工作](http://git.jd.com/jdwl_tc/JavaCourse/raw/master/01%20Java基础/04%20类结构与加载机制/Java类加载器ClassLoader/8.jpg)

## 案例1

    两个类，A、B， A依赖B，B不依赖A，将两者分别放于不同的目录加载，有以下现象：1）B放于jre/lib/ext下，A放于classpath下，正常。2）A放于jre/lib/ext下，B放于classpath下，报错：java.lang.NoClassDefFoundError:B出错原因：A放于jre/lib/ext下，其ClassLoader为ExtClassLoader（可通过Class.getClassLoader()查看），B放于classpath下，其ClassLoader为AppClassLoader，ExtClassLoader是AppClassLoader的父加载器，故ExtClassLoader无法加载到B就会报错。这是个很基础的示例，同时也是很多类加载不到案例的问题模型。

## 案例2

    在JAXP（XML处理相关） 中的， javax.xml.parsers.DocumentBuilderFactory类（位于rt.jar）的 newInstance()方法用来生成一个新的 DocumentBuilderFactory的实例，此实例必需是javax.xml.parsers.DocumentBuilderFactory的实现类，它是由JAXP的SPI具体实现所提供的，如 Apache Xerces 中的 org.apache.xerces.jaxp.DocumentBuilderFactoryImpl，问题出现了，SPI 的接口是 Java 核心库的一部分，是由引导类加载器来加载的，SPI 的实现一般都是由系统类加载器来加载的，引导类加载器无法加载到SPI的实现类，那么SPI的接口如何使用SPI的实现呢？答案就是：**线程上下文类加载器**。如果不做任何的设置，线程的上下文类加载器默认就是系统上下文类加载器（AppClassLoader）。在 SPI 接口的代码中使用线程上下文类加载器，就可以成功的加载到 SPI 实现的类。


## 相关思考 
    Class文件格式
    JAVA 堆栈 堆 方法区 静态区 final static 内存分配
    值传递和引用传递
    java.lang.String的intern()方法


