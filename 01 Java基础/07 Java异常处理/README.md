# Java异常处理

> 目录：
>	
>	1. Exception的本质
>
>	2. checked Exception 和 unchecked Exception
>
>	3. 为什么不要随便扑获顶级异常
>
>	4. 关于异常的性能代价
>
>	5. 业务异常处理的技巧
>
>	6. Java7对与异常的改进
>
>   7. 异常使用经验总结


## 1. 异常的本质
从本质上讲，异常是编程语言提供的一种错误处理机制。
使用异常将错误处理代码从正常代码中分离出来，使那些执行概率高的代码封装在一个try块内，然后将异常处理代码----这些代码是不经常执行的----置于catch子句中，这种方式的好处是，正常代码因此而更简洁。

如果方法遇到一个不知道如何处理的意外情况，那么应该抛出异常。

在有充足理由将某情况视为该方法的典型功能（typical functioning ）部分时，避免使用异常。

异常机制的引入的原因：

把错误处理和真正的工作分开来 

系统更安全，不会因为小的疏忽使程序意外崩溃，提供更加可靠的异常处理模型

支持分层嵌套：

      程序的控制流可以安全的跳转到上层（或上上层）的错误处理模块中去   

      不同于return语句，异常处理的控制流是可以安全地跨越一个或多个函数 


## 2. checked Exception 和 unchecked Exception

总的原则：

如果客户端期望并能够从异常中恢复，使用检查型异常；

如果客户端对于从异常中恢复无能为力，使用非检查型异常；

经验：

1. 通常将来自外部，不可控因素导致的异常定义为可检查异常 
FileNotFoundException，SocketException，BindException，SAXParseException，SQLException，RMIException，JMSException

2. 来自内部，“失误”导致的异常为运行时异常（不可检查异常）
IllegalArgumentException，NullPointerException，ClassCastException，IndexOutOfBoundsException，UnsupportedOperationException


## 3. 为什么不要随便扑获顶级异常

从异常的继承关系可以看到：所有unchecked exception都是RuntimeException的子类，RuntimeException又继承Exception，因此捕获Exception，同样也捕获了RuntimeException。

异常的类继承模型：
![img](http://img12.360buyimg.com/devfe/jfs/t25003/361/822682417/241929/b2301e49/5b7d1f02N0dbee633.png)

## 4. 关于异常的性能代价
用一个例子来测试异常的性能，结论是建立一个异常对象，是建立一个普通Object耗时的约20倍左右。这也是我们强调不要用异常去控制流程的一个重要原因。

另一极端是因为异常消耗性能高，刻意不去使用异常，也是错误的。

异常用于处理意外情况，非典型功能，因此异常并不会对正常的业务产生影响，当然如果一个异常业务中频繁发生，那通常不能把它归为意外情况，而是作为典型的分支来处理。

测试代码：
```
public class ExceptionTest {  
  
    private int testTimes;  
  
    public ExceptionTest(int testTimes) {  
        this.testTimes = testTimes;  
    }  
  
    public void newObject() {  
        long l = System.nanoTime();  
        for (int i = 0; i < testTimes; i++) {  
            new Object();  
        }  
        System.out.println("建立对象：" + (System.nanoTime() - l));  
    }  
  
    public void newException() {  
        long l = System.nanoTime();  
        for (int i = 0; i < testTimes; i++) {  
            new Exception();  
        }  
        System.out.println("建立异常对象：" + (System.nanoTime() - l));  
    }  
  
    public void catchException() {  
        long l = System.nanoTime();  
        for (int i = 0; i < testTimes; i++) {  
            try {  
                throw new Exception();  
            } catch (Exception e) {  
            }  
        }  
        System.out.println("建立、抛出并接住异常对象：" + (System.nanoTime() - l));  
    }  
  
    public static void main(String[] args) {  
        ExceptionTest test = new ExceptionTest(10000);  
        test.newObject();  
        test.newException();  
        test.catchException();  
    }  
}  
```

## 5. 业务异常处理的技巧
异常处理理论的两种基本模型：
>
一种称为终止模型：在这种模型中，将假设错误非常关键，以至于程序无法返回到异常发生的地方继续执行，一旦异常被抛出，就表明错误已无法挽回，也不能回来继续执行；
>
另一种称为恢复模型：意思是异常处理程序的工作是修正错误，然后重新尝试调用出问题的方法，并认为第二次能成功。对于恢复模型，我们希望异常被处理之后，能继续执行程序；

在实际项目中，异常设计要注意下面的几点：

一、自定义异常父类的选择

A、自定义异常的父类，可以选择为RuntimeException或Exception。RuntimeException是运行时异常，你可以选择它来做为你的异常父类，因为这种异常不受到编译器检查，因此，给予了程序员很大的灵活性，程序员可以处理这种异常，也可以不处理（实际上并不是不处理，而是 不立即处理，等到一个合适的地方再进行处理）。选择RuntimeException作为父类，是很多框架常采用的，如果你是做底层框架的，可以选择 RuntimeException。

B、业务层异常，一般选择Exception作为父类，因为业务层异常比较重要，一般都是要由调用者进行处理或者是要告知调用者会发生这种异常。如果你的 代码是提供给第三方厂商用的，业务层封闭统一的异常就显得非常的有必要。这类异常会强制要求程序员进行处理（异常转译或继续声明抛出），程序完整性、健壮 性得到了加强。

二、业务层自定义异常结构的设计

A、业务层自定义异常可以考虑按子系统来划分，也就是说，每一个子系统（模块）都有自己的异常定义，每个子系统自己维护自己的，统一向调用者抛出；

B、根据业务类型，从逻辑上划分异常类型，比如：权限相关的，安全相关的，数据库相关的等等；

总的来说，这两种自定义异常也可以混合使用，因为有的时候，子系统（业务模块）本身就是从逻辑上进行划分的。

三、异常结构定义

异常类的父类选定后，再定义自己的异常结构。一般的异常类中，要定义这么一些东西。

A、描述字符串，说明异常的起因，或说明等；

B、异常码。定义一个int或String类型的异常码，异常码在整个系统中统一定义，根据异常继承结构，异常码也可以定义得有层次结构。异常码在大的系统中比较常见，Oracle ，Sqlserver等数据库产品中；

C、定义一个Throwable的成员变量，用以封装异常结构链；

D、定义无参数、有参数（String，Throwable）的构造方法。

四、在WEB三层模型中，异常的处理

在经典的三层架构模型中，通常都是这样来进行异常处理的：

A、持久层一般抛出的是RuntionException类型的异常，一般不处理，直接向上抛出；

B、业务层一般要封装自定义异常，统一向外抛出（这里要注意，如果用spring在业务层管理异常，一定要配置好异常回滚类型，因为spring默认只回滚RuntionException类型的）。也有一些想省事的，业务层也不定义任何异常，也不进行try catch，如果业务层出现异常将在表现层进行处理及页面跳转；

C、控制层必须要处理业务层的异常，以正确向客户报告系统出现的问题，这里面是最后一道异常处理的地方。

![img](http://img30.360buyimg.com/devfe/s400x500_jfs/t24514/357/2363445513/92761/b964e812/5b7cda90Nfcb3da20.png)

## 6. Java 7对异常处理的改进

在java 7中，在一个单独的catch块中可以捕获许多异常，例如：
```
Java 6：
catch (IOException ex) {
     logger.error(ex);
     throw new MyException(ex.getMessage());
catch (SQLException ex) {
     logger.error(ex);
     throw new MyException(ex.getMessage());
}
```

```
Java 7:
catch(IOException | SQLException ex){
     logger.error(ex);
     throw new MyException(ex.getMessage());
}
```

在Java6及之前的版本中，异常处理时，需要使用finally块来关闭资源，如果忘记关闭，当资源被耗尽的时候会出现运行时异常。在Java 7中，如果创建一个资源在try的声明中，并且使用这个资源在try-catch块里面，当运行到这个try-catch之外的时候，运行环境会自动关闭这些资源，例如：

Java 6 资源管理的例子

```
package com.journaldev.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Java6ResourceManagement {

	public static void main(String[] args) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("C:\\journaldev.txt"));
			System.out.println(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
```

Java 7 资源管理的例子

```
package com.journaldev.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Java7ResourceManagement {

	public static void main(String[] args) {
		try (BufferedReader br = new BufferedReader(new FileReader(
				"C:\\journaldev.txt"))) {
			System.out.println(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
```
## 异常使用经验总结：
《Effective Java》中关于异常处理的条款：

1. 只针对不正常的条件才使用异常（第39条 57）
2. 对于可恢复的条件使用被检查的异常，对于程序错误使用运行时异常（第40条 58）
3. 避免不必要地使用被检查的异常（第41条 59）
4. 尽量使用标准的异常（第42条 60）
5. 抛出的异常要适合于相应的抽象（第43条 61）
6. 每个方法抛出的异常都要有文档（第44条 62）
7. 在细节消息中包含失败－捕获信息（第45条 63）
8. 努力使失败保持原子性（第46条 64）
9. 不要忽略异常（第47条 65）


异常使用经验总结：

1. 不要因异常可能对性能造成负面影响而使用错误码；

2. 避免太深的异常继承层次；

3. 要使用合理的，最具针对性的（最底层派生类）异常；

4. 不要在在异常消息中泄漏安全信息；
把与安全性有关的信息保存在私有的异常状态中，并确保只有可信赖的代码才能访问

5. 不要让调用方根据某个选项来决定是否抛出异常；

6. 避免显示的从finally块中抛出异常；

7. 不要为了仅仅为了通报错误而创建新的异常类型；
不要创建新的异常类型——如果对该错误的处理和对框架中已有异常的并没有什么不同；

8. 不要把特定的异常转化为更通用的异常；

9.  不要在自己定义的公共API里显式或隐式的抛出 ArithmeticExecption，NullPointerException等运行时异常；

10. 禁止在finally内部使用return语句，也不建议在catch块中用return的方式处理；

