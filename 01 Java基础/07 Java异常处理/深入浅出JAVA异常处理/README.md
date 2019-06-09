# 深入浅出JAVA异常处理

[TOC]

## 1. Java异常的分类和类结构图
&ensp;&ensp;&ensp;&ensp;异常处理机制能让程序在异常发生时，按照代码的预先设定的异常处理逻辑，针对性地处理异常，让程序尽最大可能恢复正常并继续执行，且保持代码的清晰。
&ensp;&ensp;&ensp;&ensp;Java中的异常可以是函数中的语句执行时引发的，也可以是程序员通过throw 语句手动抛出的，只要在Java程序中产生了异常，就会用一个对应类型的异常对象来封装异常，JRE就会试图寻找异常处理程序来处理异常。

### 1.1.按照Java类结构分类
&ensp;&ensp;&ensp;&ensp;Java标准库内建了一些通用的异常，这些类以 Throwable 为顶层父类。Throwable又派生出Error类和Exception类。

#### 1.1.1.Error类
&ensp;&ensp;&ensp;&ensp;Error类以及他的子类的实例，代表了JVM本身的错误。错误不能被程序员通过代码处理，Error很少出现。

#### 1.1.2.Exception类
&ensp;&ensp;&ensp;&ensp;Exception以及他的子类，代表程序运行时发送的各种不期望发生的事件。可以被Java异常处理机制使用，是异常处理的核心。

![异常类层次结构](img/异常类层次结构图.png "异常类层次结构")


### 1.2.根据编译器对异常的处理要求分类

#### 1.2.1.非检查异常（unckecked exception）
&ensp;&ensp;&ensp;&ensp;Error 和 RuntimeException 以及他们的子类。javac在编译时，不会提示和发现这样的异常，不要求在程序处理这些异常。可以编写代码处理（使用try…catch…finally）这样的异常，也可以不处理。这样的异常发生的原因多半是代码写的有问题，应该修正代码，而不是去通过异常处理器处理。
&ensp;&ensp;&ensp;&ensp;例如：除0错误ArithmeticException，错误的强制类型转换错误ClassCastException，数组索引越界ArrayIndexOutOfBoundsException，使用了空对象NullPointerException等。

#### 1.2.2.检查异常（checked exception）
&ensp;&ensp;&ensp;&ensp;除了Error 和 RuntimeException的其它异常。javac强制要求程序员为这样的异常做预备处理工作（使用try…catch…finally或者throws）。
&ensp;&ensp;&ensp;&ensp;在方法中要么用try-catch语句捕获它并处理，要么用throws子句声明抛出它，否则编译不会通过。这样的异常一般是由程序的运行环境导致的。
&ensp;&ensp;&ensp;&ensp;因为程序可能被运行在各种未知的环境下，而程序员无法干预用户如何使用他编写的程序，于是程序员就应该为这样的异常时刻准备着。
如SQLException , IOException,ClassNotFoundException 等。

## 2.异常处理的基本语法
### 2.1.处理异常 try-catch-finally
#### 2.1.1.基本结构
&ensp;&ensp;&ensp;&ensp;在函数块中可以使用 try…catch…finally处理异常
~~~java
public  class Test {
    public void func(){
            try{
                 //try块中放可能发生异常的代码。
                 //如果执行完try且不发生异常，则接着去执行finally块和finally后面的代码（如果有的话）。
                 //如果发生异常，则尝试去匹配catch块。             
            }catch(SQLException SQLexception){
                //每一个catch块用于捕获并处理一个特定的异常，或者这异常类型的子类。Java7中可以将多个异常声明在一个catch中。
                //catch后面的括号定义了异常类型和异常参数。如果异常与之匹配且是最先匹配到的，则虚拟机将使用这个catch块来处理异常。
                //在catch块中可以使用这个块的异常参数来获取异常的相关信息。异常参数是这个catch块中的局部变量，其它块不能访问。
                //如果当前try块中发生的异常在后续的所有catch中都没捕获到，则先去执行finally，然后到这个函数的外部caller中去匹配异常处理器。
                //如果try中没有发生异常，则所有的catch块将被忽略。 
            }catch(Exception exception){
                //...
            }finally{
                //finally块通常是可选的。
                //无论异常是否发生，异常是否匹配被处理，finally都会执行。
                //一个try至少要有一个catch块，否则， 至少要有1个finally块。但是finally不是用来处理异常的，finally不会捕获异常。
                //finally主要做一些清理工作，如流的关闭，数据库连接的关闭等。 
            }
    }
}
~~~
#### 2.1.2.finally块和return

&ensp;&ensp;&ensp;&ensp;在 try块中即便有return，break，continue等改变执行流的语句，finally模块中的代码总是会在函数执行结束前被最后执行，并会覆盖或者抑制try/catch模块中的抛出的异常和返回值。
注：一般不应在finally模块中抛出异常或者返回结果。

&ensp;&ensp;&ensp;&ensp;如下几个例子：

 finally被最后执行

~~~java
public  class Test {
    public static void main(String[] args)
    {
        int re = bar();
        System.out.println(re);//输出结果是：finally 5
    }
    private static int bar() 
    {
        try{
            return 5;
        } finally{
            System.out.println("finally");
        }
    }
}
~~~
finally返回结果被覆盖
~~~java
public class Test{
        public static void main(String[] args)
        {
            int result;     
            result  =  foo();
            System.out.println(result);     /////////2
     
            result = bar();
            System.out.println(result);    /////////2
        }     
        @SuppressWarnings("finally")
        public static int foo()
        {
            try{
                int a = 5 / 0;
            } catch (Exception e){
                return 1;
            } finally{
                return 2;
            }     
        }
     
        @SuppressWarnings("finally")
        public static int bar()
        {
            try {
                return 1;
            }finally {
                return 2;
            }
        }
}
~~~
catch中抛出的异常失效
~~~java
class TestException
{
    public static void main(String[] args)
    {
        int result;
        try{
            result = foo();
            System.out.println(result);           //输出100
        } catch (Exception e){
            System.out.println(e.getMessage());    //没有捕获到异常
        }
 
        try{
            result  = bar();
            System.out.println(result);           //输出100
        } catch (Exception e){
            System.out.println(e.getMessage());    //没有捕获到异常
        }
    }
 
    //catch中的异常被抑制
    @SuppressWarnings("finally")
    public static int foo() throws Exception
    {
        try {
            int a = 5/0;
            return 1;
        }catch(ArithmeticException amExp) {
            throw new Exception("我将被忽略，因为下面的finally中使用了return");
        }finally {
            return 100;
        }
    } 
    //try中的异常被抑制
    @SuppressWarnings("finally")
    public static int bar() throws Exception
    {
        try {
            int a = 5/0;
            return 1;
        }finally {
            return 100;
        }
    }
}
~~~
catch中抛出的异常被覆盖
~~~java
class TestException
{
    public static void main(String[] args)
    {
        int result;
        try{
            result = foo();
        } catch (Exception e){
            System.out.println(e.getMessage());//输出：我是finaly中的Exception
        }
 
        try{
            result  = bar();
        } catch (Exception e){
            System.out.println(e.getMessage());//输出：我是finaly中的Exception
        }
    }
 
    //catch中的异常被抑制
    @SuppressWarnings("finally")
    public static int foo() throws Exception
    {
        try {
            int a = 5/0;
            return 1;
        }catch(ArithmeticException amExp) {
            throw new Exception("我将被忽略，因为下面的finally中抛出了新的异常");
        }finally {
            throw new Exception("我是finaly中的Exception");
        }
    }
 
    //try中的异常被抑制
    @SuppressWarnings("finally")
    public static int bar() throws Exception
    {
        try {
            int a = 5/0;
            return 1;
        }finally {
            throw new Exception("我是finaly中的Exception");
        }
 
    }
}
~~~


### 2.2.抛出异常 throws和throw
&ensp;&ensp;&ensp;&ensp;throws声明：如果一个方法内部的代码会抛出检查异常，而方法自己又没有处理掉，javac保证你必须在方法的签名上使用throws关键字声明这些可能抛出的异常，否则编译不通过。throws它不同于try…catch…finally，throws仅仅是将函数中可能出现的异常向调用者声明，而自己则不具体处理。采取这种异常处理的原因可能是：方法本身不知道如何处理这样的异常，或者说让调用者处理更好，调用者需要为可能发生的异常负责。

&ensp;&ensp;&ensp;&ensp;throw 声明：程序员也可以通过throw语句显式的抛出一个异常,其后面必须是一个异常对象。throw 语句必须写在函数中，执行throw 语句的地方就是一个异常抛出点，它和由JRE自动形成的异常抛出点没有任何差别。

~~~
public void func() throws ExceptionType1 , ExceptionType2 ,ExceptionTypeN
{ 
     //内部可以抛出 ExceptionType1 , ExceptionType2 ,ExceptionTypeN 类的异常，或者他们的子类的异常对象。
     if (condition1){
        //do something
        throw new ExceptionType1()
     }
}
~~~


## 3.异常的几个特性
### 3.1.异常源的链式传递
&ensp;&ensp;&ensp;&ensp;在一些大型的，模块化的软件开发中，一旦一个地方发生异常，则如骨牌效应一样，将导致一连串的异常。
&ensp;&ensp;&ensp;&ensp;假设B模块完成自己的逻辑需要调用A模块的方法，如果A模块发生异常，则B也将不能完成而发生异常，但是B在抛出异常时，会将A的异常信息掩盖掉，这将使得异常的根源信息丢失。而异常的链式传递可以将多个模块的异常串联起来，使得异常信息不会丢失。

&ensp;&ensp;&ensp;&ensp;异常链式传递:以一个异常对象为参数构造新的异常对象，新的异对象将包含先前异常的信息；这主要是异常类的一个带Throwable参数的函数来实现的，这个当做参数的异常，也叫根源异常（cause）。

&ensp;&ensp;&ensp;&ensp;查看Throwable类源码，可以发现里面有一个Throwable字段cause，就是它保存了构造时传递的根源异常参数，这种设计和链表的结点类设计非常相似。
~~~java
public class Throwable implements Serializable {
    private Throwable cause = this;
 
    public Throwable(String message, Throwable cause) {
        fillInStackTrace();
        detailMessage = message;
        this.cause = cause;
    }
     public Throwable(Throwable cause) {
        fillInStackTrace();
        detailMessage = (cause==null ? null : cause.toString());
        this.cause = cause;
    }
 
    //........
}
~~~
&ensp;&ensp;&ensp;&ensp;比如抛出的异常堆栈：
~~~
java.lang.Exception: 计算失败
    at practise.ExceptionTest.add(ExceptionTest.java:53)
    at practise.ExceptionTest.main(ExceptionTest.java:18)
Caused by: java.util.InputMismatchException
    at java.util.Scanner.throwFor(Scanner.java:864)
    at java.util.Scanner.next(Scanner.java:1485)
    at java.util.Scanner.nextInt(Scanner.java:2117)
    at java.util.Scanner.nextInt(Scanner.java:2076)
    at practise.ExceptionTest.getInputNumbers(ExceptionTest.java:30)
    at practise.ExceptionTest.add(ExceptionTest.java:48)
    ... 1 more
~~~
### 3.2.异常的多态特性
&ensp;&ensp;&ensp;&ensp;当子类重写父类的带有 throws声明的函数时，其throws声明的异常必须在父类异常的可控范围内--用于处理父类的throws方法的异常处理器，必须也适用于子类的这个带throws方法 。

&ensp;&ensp;&ensp;&ensp;如，父类throws IOException，子类就必须throws IOException或者IOException的子类，而不能throw IOException的父类,如下定义父子类异常是错误的：
~~~java
class Father
{
    public void start() throws IOException
    {
        throw new IOException();
    }
} 
class Son extends Father
{
    public void start() throws Exception
    {
        throw new SQLException();
    }
}
~~~
### 3.3.异常的线程独立特性
&ensp;&ensp;&ensp;&ensp;Java程序可以是多线程的，每一个线程都是一个独立的执行流，独立的函数调用栈。如果程序只有一个线程，那么没有被任何代码处理的异常会导致程序终止。如果是多线程的，那么没有被任何代码处理的异常仅仅会导致异常所在的线程结束。因此，Java中的异常是线程独立的，线程的问题由线程自己处理，也不会直接影响到其它线程的执行。

## 4.如何自定义异常
&ensp;&ensp;&ensp;&ensp;如果要自定义检查异常，则扩展Exception类即可；如果要自定义非检查异常，则扩展自RuntimeException。

&ensp;&ensp;&ensp;&ensp;自定义的异常一般总是包含如下的构造函数：

* 一个无参构造函数，并调用父类的构造函数。
* 一个带有String参数的构造函数，并传递给父类的构造函数。
* 一个带有String参数和Throwable参数，并都传递给父类构造函数。
* 一个带有Throwable 参数的构造函数，并传递给父类的构造函数。

&ensp;&ensp;&ensp;&ensp;下面是IOException类的完整源代码，可以借鉴：
~~~java
public class IOException extends Exception
{
    static final long serialVersionUID = 7818375828146090155L;
 
    public IOException()
    {
        super();
    }
 
    public IOException(String message)
    {
        super(message);
    }
 
    public IOException(String message, Throwable cause)
    {
        super(message, cause);
    }
 
    public IOException(Throwable cause)
    {
        super(cause);
    }
}
~~~
## 5.Java异常处理最佳实践
&ensp;&ensp;&ensp;&ensp;这里收集了 Java 编程中异常处理的 10 个最佳实践。大家对 Java 中的受检异常（checked Exception）褒贬不一，这种语言特性要求该异常必须被处理。我们尽可能少使用受检异常，同时也要学会在 Java 编程中，区别使用受检和非受检异常。

1）为可恢复的错误使用受检异常，为编程错误使用非受检异常。

&ensp;&ensp;&ensp;&ensp;对 Java 开发者来说，选择受检还是非受检异常总是让人感到困惑。受检异常保证你会针对错误情况提供异常处理代码，这是一种从语言层面上强制你编写健壮代码的一种方式，但同时也引入大量杂乱的代码并导致其可读性变差。
当然，如果你有可替代方式或恢复策略的话，捕获异常并做处理看起来似乎也合情合理。

2）在 finally 程序块中关闭或者释放资源

&ensp;&ensp;&ensp;&ensp;这是 Java 编程中一个广为人知的最佳实践和一个事实上的标准，尤其是在处理网络和 IO 操作的时候。在 finally 块中关闭资源能保证无论是处于正常还是异常执行的情况下，资源文件都能被合理释放，这由 finally 语句块保证。
&ensp;&ensp;&ensp;&ensp;从 Java7 开始，新增加了一项更有趣的功能：自动资源管理，或者称之为ARM块。尽管如此，我们仍然要记住在 finally 块中关闭资源，这对于释放像 FileDescriptors 这类资源至关重要，因为它在 socket 和文件操作中都会被用到。

3）在堆栈信息中包含引起异常的原因

&ensp;&ensp;&ensp;&ensp;Java 库和开源代码在很多情况下会将一种异常包装成另一种异常。这样记录和打印根异常就变得非常重要。Java 异常类提供了 getCause() 方法来获取导致异常的原因，这可以提供更多有关异常发生的根本原因的信息。
&ensp;&ensp;&ensp;&ensp;这条实践对调试或排除故障大有帮助。在把一个异常包装成另一种异常时，记住需要把源异常传递给新异常的构造器。

4）始终提供异常的有意义的完整信息

&ensp;&ensp;&ensp;&ensp;异常信息是最重要的，在其中，你能找到问题产生的原因，因为这是出问题后程序员最先看到的地方。记得始终提供精确的真实的信息。例如，对比下面两条 IllegalArgumentException 的异常信息：

message 1: “Incorrect argument for method” message 2: “Illegal value for ${argument}: ${value}

&ensp;&ensp;&ensp;&ensp;第一条消息仅说明了参数是非法的或不正确的，但第二条消息包括了参数名和非法值，这对找到错误原因很重要。在编写异常处理代码的时候，应当始终遵循该 Java 最佳实践。

5）避免过度使用受检异常

&ensp;&ensp;&ensp;&ensp;受检异常的强制性在某种程度上具有一定的优势，但同时它也使得代码可读性变差，混淆了正常的业务逻辑代码。你可以通过适度使用受检异常来最大限度地减少这类情况的发生，这样可以得到更简洁的代码。
&ensp;&ensp;&ensp;&ensp;你同样可以使用 Java7 的新功能，比如在一个catch语句中捕获多个异常，以及自动管理资源，以此来移除一些冗余的代码。

6）将受检异常转为运行时异常

&ensp;&ensp;&ensp;&ensp;这是在诸如 Spring 之类的框架中用来减少使用受检异常的方式之一，大部分 JDBC 的受检异常都被包装进 DataAccessException 中，DataAccessException异常是一种非受检异常。
&ensp;&ensp;&ensp;&ensp;这个最佳实践带来的好处是可以将特定的异常限制到特定的模块中，比如把 SQLException 抛到 DAO 层，把有意义的运行时异常抛到客户端层。

7）记住异常的性能代价高昂

&ensp;&ensp;&ensp;&ensp;需要记住的一件事是异常代价高昂，同时让代码运行缓慢。假如你有一个方法从 ResultSet 中进行读取，它经常会抛出 SQLException 而不是将 cursor 移到下一元素，这将会比不抛出异常的正常代码执行的慢的多。
&ensp;&ensp;&ensp;&ensp;因此最大限度的减少不必要的异常捕捉，去修复真正的根本问题。不要仅仅是抛出和捕捉异常，如果你能使用 boolean 变量去表示执行结果，可能会得到更整洁、更高性能的解决方案。修正错误的根源，避免不必要的异常捕捉。

8）避免空的 catch 块

&ensp;&ensp;&ensp;&ensp;没有什么比空的 catch 块更糟糕的了，因为它不仅隐藏了错误和异常，同时可能导致你的对象处于不可用状态或者脏状态。空的 catch 块没有意义，除非你非常肯定异常不会以任何方式影响对象的状态，但在程序执行期间，用日志记录错误依然是最好的方法。
这在 Java 异常处理中不仅仅是一个最佳实践，而且是一个最通用的实践。

9）使用标准异常

&ensp;&ensp;&ensp;&ensp;第九条最佳实践是建议使用标准和内置的 Java 异常。使用标准异常而不是每次创建我们自己的异常，这对于目前和以后代码的可维护性和一致性，都是最好的选择。
重用标准异常使代码可读性更好，因为大部分 Java 开发人员对标准的异常更加熟悉，比如 JDK 中的RuntimeException，IllegalStateException，IllegalArgumentException，NullPointerException，
他们能立刻知道每种异常的目的，而不是在代码或文档里查找用户自定义异常的目的。

10）为方法抛出的异常编写文档

&ensp;&ensp;&ensp;&ensp;Java 提供了 throw 和 throws 关键字来抛出异常，在 javadoc 中可以用@throw 为任何可能被抛出的异常编写文档。如果你编写 API 或者公共接口，这就变得非常重要。当任何方法抛出的异常都有相应的文档记录时，就能潜在的提醒任何调用该方法的开发者。

## 6.分层处理模型异常定义实践
&ensp;&ensp;&ensp;&ensp;这里主要讨论应用内部Service层和对外API层的异常处理实践。
### 6.1.Service层异常
#### 6.1.1.参数约束判断和技术选择
&ensp;&ensp;&ensp;&ensp;提供服务时，永远不要相信调用方发过来的数据是合法的。因此，不管是Service或者API都应该对输入数据进行必要的合法性校验。而对于这种通用的校验，我们完全可以不必重复发明轮子。Guava中的Preconditions类可以优雅的满足我们绝大分的需求，比如：
*  checkArgument(boolean) ：

　　功能描述：检查boolean是否为真。 用作方法中检查参数

　　失败时抛出的异常类型: IllegalArgumentException

*  checkNotNull(T)：   
  
　　功能描述：检查value不为null， 直接返回value；

　　失败时抛出的异常类型：NullPointerException

*  checkState(boolean)：

　　功能描述：检查对象的一些状态，不依赖方法参数。

　　失败时抛出的异常类型：IllegalStateException

*  checkElementIndex(int index, int size)：

　　功能描述：检查index是否为在一个长度为size的list， string或array合法的范围。 index的范围区间是[0, size)。

　　失败时抛出的异常类型：IndexOutOfBoundsException

*  checkPositionIndex(int index, int size)：

　　功能描述：检查位置index是否为在合法的范围。 index的范围区间是[0， size]。

　　失败时抛出的异常类型：IndexOutOfBoundsException

*  checkPositionIndexes(int start, int end, int size)：

　　功能描述：检查[start, end)是一个长度为size的list， string或array合法的范围子集。0<=start<=end<=size。

　　失败时抛出的异常类型：IndexOutOfBoundsException

示例如下：
~~~java
public class Test{
    public static void insert(String name, int age) {
        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(!name.equals(""));
        Preconditions.checkArgument(age >= 0);
        //process your business
    }
}
~~~

#### 6.1.2.抛出异常定义
##### 6.1.2.1 参数约束类型异常
&ensp;&ensp;&ensp;&ensp;一般直接采用guava 的Preconditions类非检查异常即可
##### 6.1.2.2 业务类型异常
&ensp;&ensp;&ensp;&ensp;需要自定义异常，业务类型异常一般用非检查异常。而定义业务异常一般有两种方式：
* 抛出带状态码RumtimeException异常
* 抛出指定类型的RuntimeException异常

&ensp;&ensp;&ensp;&ensp;对于Service业务类型异常一般采用后者，前者比较适合对外API模式的异常定义，例如：

~~~java
public class NotFindUserException extends RuntimeException {
        public NotFindUserException() {
            super("找不到此用户");
        }
        public NotFindUserException(String message) {
            super(message);
        }
}
~~~
 &ensp;&ensp;&ensp;&ensp;因此对于service层的异常抛出可以如下：
 ~~~java
 public class Test{
     public Address createAddress(Integer uid, Address address) {
            //============ 以下为约束条件   ==============
            //用户id不能为空，且此用户确实是存在的
            Preconditions.checkNotNull(uid);
            User user = userDao.findOne(uid);
            if(null == user){
                throw new NotFindUserException("找不到当前用户!");
            }
            //============ 以下为正常执行的业务逻辑   ==============
            address.setUser(user);
            Address result = addressDao.save(address);
            return result;
    }
}
 ~~~
### 6.2.对外API层异常
#### 6.2.1.API异常设计
&ensp;&ensp;&ensp;&ensp;API层异常一般需要提供错误码和错误信息，那么可以提供一个通用的api超类异常，其他不同的api异常都继承自这个超类:
~~~java
public class ApiException extends RuntimeException {
    protected Long errorCode ;
    protected Object data ;
    public ApiException(Long errorCode,String message,Object data,Throwable e){
        super(message,e);
        this.errorCode = errorCode ;
        this.data = data ;
    }
    public ApiException(Long errorCode,String message,Object data){
        this(errorCode,message,data,null);
    }
    public ApiException(Long errorCode,String message){
        this(errorCode,message,null,null);
    }
    public ApiException(String message,Throwable e){
        this(null,message,null,e);
    }
    public ApiException(){
    }
    public ApiException(Throwable e){
        super(e);
    }
    public Long getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
~~~
&ensp;&ensp;&ensp;&ensp;然后分别定义api层异常：
&ensp;&ensp;&ensp;&ensp;ApiDefaultAddressNotDeleteException,ApiNotFindAddressException,ApiNotFindUserException,ApiNotMatchUserAddressException。
例如:
~~~java
public class ApiDefaultAddressNotDeleteException extends ApiException { 
    public ApiDefaultAddressNotDeleteException(String message) {
     super(AddressErrorCode.DefaultAddressNotDeleteErrorCode,
           message,null);
    }
}
~~~
&ensp;&ensp;&ensp;&ensp;AddressErrorCode.DefaultAddressNotDeleteErrorCode就是需要提供给调用者的错误码。错误码类如下:
~~~java
public abstract class AddressErrorCode {
    public static final Long DefaultAddressNotDeleteErrorCode = 10001L;//默认地址不能删除
    public static final Long NotFindAddressErrorCode = 10002L;//找不到此收货地址
    public static final Long NotFindUserErrorCode = 10003L;//找不到此用户
    public static final Long NotMatchUserAddressErrorCode = 10004L;//用户与收货地址不匹配
}
~~~
注：AddressErrorCode错误码类存放了可能出现的错误码，更合理的做法是把他放到配置文件中进行管理。

#### 6.2.2.API异常转换
&ensp;&ensp;&ensp;&ensp;API层一般是调用service层实现，对service层抛出的异常需要做适当的归类和转换，一般不应该将系统敏感信息直接暴露出去，而是通过错误状态码的方式隐藏系统细节。
~~~java
@ControllerAdvice(annotations = RestController.class)
class ApiExceptionHandlerAdvice { 
    /**
     * Handle exceptions thrown by handlers.
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> exception(Exception exception,HttpServletResponse response) {
        ErrorDTO errorDTO = new ErrorDTO();
        if(exception instanceof ApiException){//api异常
            ApiException apiException = (ApiException)exception;
            errorDTO.setErrorCode(apiException.getErrorCode());
        }else{//未知异常
            errorDTO.setErrorCode(0L);
        }
        errorDTO.setTip(exception.getMessage());
        ResponseEntity<ErrorDTO> responseEntity = new ResponseEntity<>(errorDTO,HttpStatus.valueOf(response.getStatus()));
        return responseEntity;
    } 
    @Setter
    @Getter
    class ErrorDTO{
        private Long errorCode;
        private String tip;
    }
}
~~~
