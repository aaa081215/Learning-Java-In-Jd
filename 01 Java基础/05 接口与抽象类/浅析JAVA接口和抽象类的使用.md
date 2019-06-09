# 浅析JAVA接口和抽象类的使用

>此篇是在网上众多相关文章中筛选出来的不错的一篇，以后有时间再写一篇原创的

## 1.目标

1）掌握抽象类和接口的实例化操作。

2）掌握模板设计的作用。

3）掌握工厂设计模式的作用。

4）掌握代理设计模式的作用。

5）掌握适配器模式的作用。

6）掌握抽象类与接口的使用区别。

## 2.具体内容

### 2.1 为抽象类和接口实例化

　　在java中，可以通过对象的多态性，为抽象类和接口实例化，这样再使用抽象类和接口的时候就可以调用本子类中所覆写过的方法。

　　之所以抽象类和接口不能直接实例化，是因为其内部包含了抽象方法，抽象方法本身是未实现的方法，所以无法调用。

　　通过对象多态性可以发现，子类发生了向上转型关系之后，所调用的全部方法，都是被覆写过的方法。如下：


```java
abstract class A{    // 定义抽象类A
    public abstract void print() ;    // 定义抽象方法print()
};
class B extends A {    // 定义子类，继承抽象类
    public void print(){        // 覆写抽象方法
        System.out.println("Hello World!!!") ;
    }
};
public class AbstractCaseDemo01{
    public static void main(String args[]){
        A a = new B() ;        // 通过子类为抽象类实例化，向上转型。
        a.print() ;
    }
};
运行结果：
Hello World!!1
```

**可以继续利用此概念，为接口实例化**。

```java
package methoud;
interface A{    // 定义抽象类A
    public abstract void print() ;    // 定义抽象方法print()
};
class B implements A {    // 定义子类，继承抽象类
    public void print(){        // 覆写抽象方法
        System.out.println("Hello World!!!") ;
    }
};
public class ThisDemo06{
    public static void main(String args[]){
        A a = new B() ;        // 通过子类为抽象类实例化
        a.print() ;
    }
};
```

**证明，如果要想使用抽象类和接口，则只能按照以上操作完成，**

### 2.2 抽象类的实际应用--模板设计

首先看这样一个场景：假设人分学生和工人，学生和工人都可以说话，但是学生和工人说的话内容不一样，也就是说

说话这个功能应该是具体功能，而说话的内容就要由学生或工人来决定了。所以此时可以使用抽象类实现这种场景。

![1](http://img14.360buyimg.com/devfe/jfs/t23734/84/2141789111/32973/4a19cff7/5b74d9deN83355d4c.png)


```java
abstract class Person{
    private String name ;        // 定义name属性
    private int age ;            // 定义age属性
    public Person(String name,int age){
        this.name = name ;
        this.age = age ;
    }
    public String getName(){
        return this.name ;
    }
    public int getAge(){
        return this.age ;
    }
    public void say(){        // 人说话是一个具体的功能
        System.out.println(this.getContent()) ;    // 输出内容
    }
    public abstract String getContent() ;    // 说话的内容由子类决定
};
class Student extends Person{
    private float score ;
    public Student(String name,int age,float score){
        super(name,age) ;    // 调用父类中的构造方法
        this.score = score ;
    }
    public String getContent(){
        return    "学生信息 --> 姓名：" + super.getName() + 
                "；年龄：" + super.getAge() + 
                "；成绩：" + this.score ;
    }
};
class Worker extends Person{
    private float salary ;
    public Worker(String name,int age,float salary){
        super(name,age) ;    // 调用父类中的构造方法
        this.salary = salary ;
    }
    public String getContent(){
        return    "工人信息 --> 姓名：" + super.getName() + 
                "；年龄：" + super.getAge() + 
                "；工资：" + this.salary ;
    }
};
public class AbstractCaseDemo02{
    public static void main(String args[]){
        Person per1 = null ;    // 声明Person对象
        Person per2 = null ;    // 声明Person对象
      per1 = new Student("张三",20,99.0f) ;    // 学生是一个人
        per2 = new Worker("李四",30,3000.0f) ;    // 工人是一个人
        per1.say() ;    // 学生说学生的话
        per2.say() ;    // 工人说工人的话
    }
};
运行结果：
学生信息 --> 姓名：张三；年龄：20；成绩：99.0
工人信息 --> 姓名：李四；年龄：30；工资：3000.0
```


**这里，抽象类就相当于一个模板**。

就像现实中的各种模板，只有模板填写之后才会有意义。

![img](http://img12.360buyimg.com/devfe/jfs/t24142/344/2120117975/62992/311fe81a/5b74da7cN615705cb.png)

### 2.3 接口的实际应用--制定标准。（一定要按照规定的标准做）

接口在实际应用中更多的作用是来指定标准的。比如说”U盘和打印机都可以插在电脑上使用，因为他们都实现了USB标准的接口

，对于电脑来说，只要符合USB接口标准的设备都可以插进来。“

![img](http://img14.360buyimg.com/devfe/jfs/t22756/3/2070749754/68392/b2cf8db2/5b74dadfN79d95dc2.png)


```java
interface USB{        // 定义了USB接口
    public void start() ;    // USB设备开始工作
    public void stop() ;    // USB设备结束工作
}
class Computer{
    public static void plugin(USB usb){    // 电脑上可以插入USB设备，向上转型，这里就相当于一个接口，只有符合这个接口的标准的类的对象（即只有继承这个接口的类的对象），才能被这个方法调用。
        usb.start() ;
        System.out.println("=========== USB 设备工作 ========") ;
        usb.stop() ;
    }
};
class Flash implements USB{
    public void start(){    // 覆写方法
        System.out.println("U盘开始工作。") ;
    }
    public void stop(){        // 覆写方法
        System.out.println("U盘停止工作。") ;
    }
};
class Print implements USB{
    public void start(){    // 覆写方法
        System.out.println("打印机开始工作。") ;
    }
    public void stop(){        // 覆写方法
        System.out.println("打印机停止工作。") ;
    }
};
public class InterfaceCaseDemo02{
    public static void main(String args[]){
        Computer.plugin(new Flash()) ;
      Computer.plugin(new Print()) ;
    }
};
运行结果：
U盘开始工作。
=========== USB 设备工作 ========
U盘停止工作。
打印机开始工作。
=========== USB 设备工作 ========
打印机停止工作。
```

### 2.4 工厂设计模式（接口应用）

工厂设计模式是在java开发中最常使用的一种设计模式。

```java
interface Fruit{    // 定义一个水果接口
    public void eat() ;    // 吃水果
}
class Apple implements Fruit{
    public void eat(){
        System.out.println("** 吃苹果。") ;
    }
};
class Orange implements Fruit{
    public void eat(){
        System.out.println("** 吃橘子。") ;
    }
};
public class InterfaceCaseDemo03{
    public static void main(String args[]){
        Fruit f = new Apple() ;    // 实例化接口
        f.eat() ;
    }
};
运行结果：
吃苹果。
```

　　这样的代码可以使吗？有问题吗？

分析：

　　主方法：就应该表示**客户端**。主方法的代码越少越好。此时，直接在主方法中指定了要操作的子类，如果要更换子类，肯定要修改客户端。

就表示跟特定的子类耦合在一起了。

　　JVM工作原理：程序-》JVM（相当于**客户端**）-》操作系统。

问题的解决：客户端通过过渡端，得到特定子类的接口实例，返回接口实例给客户端，接口实例调用接口中的方法。

![img](http://img12.360buyimg.com/devfe/jfs/t7342/146/3126706968/61596/66423f1e/5b74db62N0b2f8dc0.png)、

　　此**过渡端**，在程序中就称为**工厂设计（工厂类）**。

```java
interface Fruit{    // 定义一个水果接口
    public void eat() ;    // 吃水果
}
class Apple implements Fruit{
    public void eat(){
        System.out.println("** 吃苹果。") ;
    }
};
class Orange implements Fruit{
    public void eat(){
        System.out.println("** 吃橘子。") ;
    }
};
class Factory{    // 定义工厂类
    public static Fruit getInstance(String className){　//注意这里的方法是static修饰的，因为在主方法中是Factory调用
        Fruit f = null ;
        if("apple".equals(className)){    // 判断是否要的是苹果的子类
            f = new Apple() ;
        }
        if("orange".equals(className)){    // 判断是否要的是橘子的子类
            f = new Orange() ;
        }
        return f ;
    }
};
public class InterfaceCaseDemo04{
    public static void main(String args[]){
        Fruit f = Factory.getInstance(”apple“) ;    // 实例化接口
        f.eat() ;
    }
};
运行结果：
吃苹果
```

![img](http://img30.360buyimg.com/devfe/jfs/t24799/196/582210080/45445/a8d450e3/5b74dbb9Nc1647780.png)

　　**流程是**：客户端（主方法）通过工厂类的getInstance()方法，通过传入的参数判断，该获取实例化哪个子类的实例，

然后把获取的实例通过getInstance()方法返回（return）该实例给主方法中的接口实例，最后通过接口实例调用所需方法。

### 2.5 代理设计模式（接口应用）

　　假设现在有以下这种情况：

1）张三借了李四500块钱。

2）李四不换，张三生气。

3）张三找到王五，王五是讨债公司。

4）王五准备了刀枪

5）把李四欠的钱还回来了。

也就是张三找李四借钱，王五代理了张三。

　　代理设计：也是在java中应用较多的设计模式，所谓的代理设计就是指一个代理主题来操作真实主题，真实主题执行具体的业务操作，

而代理主题负责其他相关业务。就好比生活中经常用到的代理上网那样，客户通过网络代理连接网络，由代理服务器完成用户权限，上网操作相关操作。

![img](http://img12.360buyimg.com/devfe/jfs/t25441/39/607127507/47212/b7bfb03e/5b74dd01N5a8915f1.png)


　　分析结果：不管代理操作也好，真实操作也好，其共同目的就是上网，所以用户关心的是如何上网，至于如何操作的，用户不关心。

![img](http://img14.360buyimg.com/devfe/ds_image/t1/26/36/810/13361/5b74e2a2Efbe3a600/5157901190e80e91.png)

```java
interface Network{　　
    public void browse() ;    // 浏览
}
class Real implements Network{　　//真实上网操作类
    public void browse(){
        System.out.println("上网浏览信息") ;
    }
};
class Proxy implements Network{　　//代理类。
   private Network network ;    // 代理对象
    public Proxy(Network network){　　//初始化，把真实对象传给代理对象，向上转型操作。
        this.network = network ;
    }
    public void check(){
        System.out.println("检查用户是否合法。") ;
    }
    public void browse(){
        this.check() ;
        this.network.browse() ;    // 调用真实的主题操作，这里调用的是真实类里的对象。
    }
};
public class ProxyDemo{
    public static void main(String args[]){
        Network net = null ;
        net  = new Proxy(new Real()) ;//  指定代理操作，这里两次向上转型操作，第一次向上是实例化代理类，
第二次向上转型是括号中，把真实类对象传入，以便在代理类中调用真实类中的方法。

        net.browse() ;    // 客户只关心上网浏览一个操作
    }
};
运行结果：
检查用户是否合法
上网浏览信息
```

![img](http://img30.360buyimg.com/devfe/jfs/t25414/231/554152958/41459/9cfe5513/5b74e2f0Nfa70f6a7.png)

### 2.6适配器模式

　　此设计，在日后学习java 的图形界面用的非常多。

　　在Java中，一个子类实现了一个接口，则肯定在子类中覆写此接口中全部抽象方法。那么这样一来，

如果一个接口中提供的抽象方法过多，而且没有必要全部实现的话，肯定很浪费，此时就需要一个中间过渡，

但是此过渡又不希望被直接调用，所以**将此过渡器定义成抽象类**更合适，即：一个接口首先被一个抽象类（此抽象类成为适配器类）继承，

并在此抽象类中**实现**若干方法（方法体为空），则以后的子类直接继承此抽象类（适配器），就可以有选择的覆写所需方法了。

**一个抽象类可以实现一个接口，那么对于抽象类的子类，则必须覆写抽象类和接口的全部（未实现的）抽象方法。（即如果接口中的某类方法在抽象类中实现了（覆写了），那么在抽象类的子类中就可以不用覆写了。因为继承，在父类中的方法，被子类继承了，相当于子类覆写了，子类照样可以调用，覆写该方法。）**

![img](http://img14.360buyimg.com/devfe/jfs/t23788/7/2126193726/12334/f9613af4/5b74e3c9N145f0103.png)


```java
interface Window{        // 定义Window接口，表示窗口操作
    public void open() ;    // 打开
    public void close() ;    // 关闭
    public void activated() ;    // 窗口活动
    public void iconified() ;    // 窗口最小化
    public void deiconified();// 窗口恢复大小
}
abstract class WindowAdapter implements Window{
    public void open(){} ;    // 打开
    public void close(){} ;    // 关闭
    public void activated(){} ;    // 窗口活动
    public void iconified(){} ;    // 窗口最小化
    public void deiconified(){};// 窗口恢复大小
};
class WindowImpl extends WindowAdapter{
    public void open(){
        System.out.println("窗口打开。") ;
    }
    public void close(){
        System.out.println("窗口关闭。") ;
    }
};
public class AdapterDemo{
    public static void main(String args[]){
        Window win = new WindowImpl() ;
        win.open() ;
        win.close() ;
    }
};
运行结果：
窗口打开。
窗口关闭。
```

此种设计思路，在java的图形界面编程上使用的非常多。但是在javaee上使用不常见。

## 3.接口和抽象之间的关系。

![img](http://img10.360buyimg.com/devfe/jfs/t22624/156/2143285004/95634/b93fa1c/5b74e44bN45e94a44.png)

##  重要提示：

**在开发中，一个类永远不要去继承一个已经实现好的类，要么继承抽象类，要么实现接口，如果两个类同时都**

**可以使用的话，优秀使用接口，避免单继承的局限。**

## 总结

1）抽象类和接口类的实例化，通过多态性（向上转型，向下转型）。

2）抽象类表示一个模板，接口表示一个标准。

3）常见的设计模式：模板设计，工厂设计，代理设计，适配器设计