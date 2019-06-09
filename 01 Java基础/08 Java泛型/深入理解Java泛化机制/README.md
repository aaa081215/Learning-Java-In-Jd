# 深入理解Java泛型机制
> Java 泛型（generics）是JDK 5中引入的一个新特性。本文将带大家理解什么是泛型，以及学习和应用泛型是所必备的知识点。

##  什么是泛型

泛型本质是为了“参数化类型”，也就是说在泛型使用过程中，操作的数据类型被指定为一个参数，这种参数类型可以用在类、接口和方法中，分别被称为泛型类、泛型接口、泛型方法。 

泛型的使用为我们带来了以下好处：
1. 在编译阶段进行更强大的类型检查。
2. 避免强制类型转换
3. 使程序员可以实现更通用的算法。

##  泛型基础

> 泛型类型、通配符、泛型方法、类型推断、类型擦除、泛型限制

### 泛型类型、通配符：
#### 相关概念
泛化类型：泛型类型是通过类型参数化的泛型类或接口，比如下面代码中E
原始类型：没有任何类型参数的泛型类型（如Collection）称为原始类型。比如下面代码中的ArrayList 
参数化类型：具有实际类型参数的泛型类型的实例化称为参数化类型。比如下面代码中 ArrayList&lt;String&gt; 的String

~~~java
	interface Collection<E>  {  
      public void add (E x);  
      public Iterator<E> iterator(); 
    }
	ArrayList rawList = new ArrayList();  //原始类型
	ArrayList<String> stringList = new ArrayList<String>(); 
	rawList = stringList; 
	stringList = rawList;      // unchecked warning
~~~

#### 泛化类型的命名规范
- E - Element (used extensively by the Java Collections Framework)
- K - Key
- N - Number
- T - Type
- V - Value
- S,U,V etc. - 2nd, 3rd, 4th types

#### 通配符与上下限
有界泛型：如果要声明一个有界泛型，只需要确定该泛型继承自哪个对象通过extends关键字进行声明即可。比如以下代码示例中 &lt;U extends Number&gt;
~~~java
public class Box<T extends Number> {
    private T t;          
    public void set(T t) {
        this.t = t;
    }
    public T get() {
        return t;
    }
    public <U extends Number> void inspect(U u){
        System.out.println("T: " + t.getClass().getName());
        System.out.println("U: " + u.getClass().getName());
    }
    public static void main(String[] args) {
        Box<Integer> integerBox = new Box<Integer>();
        integerBox.set(new Integer(10));
        integerBox.inspect("some text"); // error: this is still String!
    }
}
~~~

通配符：在使用泛型类的时候，既可以指定一个具体的类型，也可以用通配符?来表示未知类型。比如：List&lt;?&gt;所声明的就是所有类型都是可以的。但是List&lt;?&gt;并不等同于List&lt;Object&gt;。List&lt;Object&gt;实际上确定了List中包含的是Object及其子类，在使用的时候都可以通过Object来进行引用。而List&lt;?&gt;则其中所包含的元素类型是不确定。其中可能包含的是String，也可能是 Integer。正因为类型未知，就不能通过new ArrayList&lt;?&gt;()的方法来创建一个新的ArrayList对象。因为编译器无法知道具体的类型是什么。但是对于 List&lt;?&gt;中的元素确总是可以用Object来引用的，因为虽然类型未知，但肯定是Object及其子类。

上限有界通配符：可以使用上限通配符来放宽对变量的限制。在一些使用场景中，要编写适用于Number的List和Number的子类型的方法，例如Integer，Double和Float，您可以指定List &lt;？ extends Number&gt;。 List &lt;? extends Number&gt;比List &lt;Number&gt;更具有扩展性，因为前者匹配了Number类型以及其子类类型，而后者仅仅匹配Number类型。

~~~java
import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void process(List<? extends Number> list) {
        for (Number number : list) {
            System.out.println(number.getClass().getTypeName());
        }
    }

    public static void process2(List<Number> list) {
        for (Number number : list) {
            System.out.println(number.getClass().getTypeName());
        }
    }

    public static void main(String[] args) {
        List<Integer> integers = new ArrayList<>();
        integers.add(new Integer(1));

        Demo.process(integers);//OK 因为process传入的是Number的子类型

        Demo.process2(integers);// ERROR 因为process2需要传入的类型为Number
    }

}
~~~

下限有界通配符：与上限有界通配符相似。下限有界通配符将未知的类型限制为该类型的特定类型或者超类类型。使用关键字super。
~~~java
    public static void addNumbers(List<? super Integer> list) {
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
    }
~~~


#### 泛型接口
泛型类型用于接口的定义中。泛型接口常用在各种类的生成器中。
~~~java
public interface Iterable<T> {
    Iterator<T> iterator();
    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : this) {
            action.accept(t);
        }
    }
    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
}
~~~

那么实现或者继承泛型接口的类或接口时如果不传入实际类型参数时，需要与泛型接口的泛型类型定义相同并且需要声明。

~~~java
public interface Collection<E> extends Iterable<E> {
    boolean add(E e);
    boolean remove(Object o);
    //略
}

~~~

如果实现泛型接口的类，传入实际类型参数时，所有的E都要替换成实际类型：

~~~java
public interface DemoCollection extends Iterable<String> {
    boolean add(String e);
    boolean remove(Object o);
    //略
}
~~~

#### 泛型类
泛型类型用于类的定义中，通过泛型可以完成对一组类的操作对开发相同的接口。

~~~java
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(K key) { this.key = key; }
    public void setValue(V value) { this.value = value; }
    public K getKey()   { return key; }
    public V getValue() { return value; }
}
~~~

#### 泛型方法

 泛型类，是在实例化类的时候指明泛型的具体类型；泛型方法，是在调用方法的时候指明泛型的具体类型。 

~~~java
 public class Utils {

    /**
     * 泛型方法
     *
     * @param c
     *         用来创建泛型T代表的类对象
     * @param <T>
     *         声明一个泛型T
     *
     * @return
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T getObject(Class<T> c) throws IllegalAccessException, InstantiationException {
        T t = c.newInstance();
        return t;
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Utils utils = new Utils();

        String str = utils.getObject(String.class);

        System.out.println(str);

    }

}
~~~

说明一下，定义泛型方法时，必须在返回值前边加一个 < T >，来声明这是一个泛型方法，持有一个泛型T，然后才可以用泛型T作为方法的返回值。
Class < T >的作用就是指明泛型的具体类型，而Class < T >类型的变量c，可以用来创建泛型类的对象。
为什么要用变量c来创建对象呢？既然是泛型方法，就代表着我们不知道具体的类型是什么，也不知道构造方法如何，因此没有办法去new一个对象，但可以利用变量c的newInstance方法去创建对象，也就是利用反射创建对象。
泛型方法要求的参数是Class< T >类型，而Class.forName()方法的返回值也是Class< T >，因此可以用Class.forName()作为参数。其中，forName()方法中的参数是何种类型，返回的Class < T >就是何种类型。在本例中，forName()方法中传入的是User类的完整路径，因此返回的是Class< User >类型的对象，因此调用泛型方法时，变量c的类型就是Class < User >，因此泛型方法中的泛型T就被指明为User，因此变量obj的类型为User。
当然，泛型方法不是仅仅可以有一个参数Class< T >，可以根据需要添加其他参数。
为什么要使用泛型方法呢？因为泛型类要在实例化的时候就指明类型，如果想换一种类型，不得不重新new一次，可能不够灵活；而泛型方法可以在调用的时候指明类型，更加灵活。

###  类型推断：

在JDK1.7之前，我们使用泛型，需要在声明并赋值的时候，两侧都要加上泛型类型
~~~java
List<String> list = new ArrayList<String>();
~~~

在JDK1.7之后，赋值的语句可以不用加泛型类型
~~~java
List<String> list = new ArrayList<>();
~~~

这就得益于类型推断。JDK8更是对泛型类型推断进行改进
1. 支持通过方法上下文推断泛型目标类型

2. 支持在方法调用链路当中，泛型类型推断传递到最后一个方法

官方例子：
~~~ java
    class List<E> {
       static <Z> List<Z> nil() { ... };
       static <Z> List<Z> cons(Z head, List<Z> tail) { ... };
       E head() { ... }
    }
    //通过方法赋值的目标参数来自动推断泛型的类型
    List<String> l = List.nil();
    //而不是显示的指定类型
    //List<String> l = List.<String>nil();
    //通过前面方法参数类型推断泛型的类型
    List.cons(42, List.nil());
    //而不是显示的指定类型
    //List.cons(42, List.<Integer>nil());
~~~

###  类型擦除：

Java中的泛型基本上都是在编译器这个层次来实现的。在生成的Java字节代码中是不包含泛型中的类型信息的。使用泛型的时候加上的类型参数，会被编译器在编译的时候去掉。这个过程就称为类型擦除 。

Java编译器将类型擦除时，如果是无界泛型，那么会将泛型类型所有类型参数替换为其边界类型或者对象。因此最终生成的字节码仅包换普通的类、接口和方法。必要的情况下，会插入类型以保持类型安全。生成桥接方法以保留扩展泛型类型中的多态性。 

擦除无界泛型类，将会用Object替换所有的类型参数。

擦除前：

~~~java
public class Node<T> {

    private T data;
    private Node<T> next;

    public Node(T data, Node<T> next) {
        this.data = data;
        this.next = next;
    }

    public T getData() { return data; }
    // ...
}
~~~

擦除后：

~~~java 
public class Node {

    private Object data;
    private Node next;

    public Node(Object data, Node next) {
        this.data = data;
        this.next = next;
    }

    public Object getData() { return data; }
    // ...
}
~~~

擦除无界泛型方法：

擦除前：

~~~java
public static <T> int count(T[] anArray, T elem) {
    int cnt = 0;
    for (T e : anArray)
        if (e.equals(elem))
            ++cnt;
        return cnt;
}
~~~

擦除后：

~~~java
public static int count(Object[] anArray, Object elem) {
    int cnt = 0;
    for (Object e : anArray)
        if (e.equals(elem))
            ++cnt;
        return cnt;
}
~~~

擦除有界泛型类：

擦除前：

~~~java
public class Node<T extends Comparable<T>> {

    private T data;
    private Node<T> next;

    public Node(T data, Node<T> next) {
        this.data = data;
        this.next = next;
    }

    public T getData() { return data; }
    // ...
}
~~~

擦除后：

~~~java
public class Node {

    private Comparable data;
    private Node next;

    public Node(Comparable data, Node next) {
        this.data = data;
        this.next = next;
    }

    public Comparable getData() { return data; }
    // ...
}
~~~

擦除有界泛型方法：

假设有以下类关系：

~~~java
class Shape { /* ... */ }
class Circle extends Shape { /* ... */ }
class Rectangle extends Shape { /* ... */ }
~~~

有个通用的方法如下：

~~~java
public static <T extends Shape> void draw(T shape) { /* ... */ }
~~~

编译擦除时，会将所有的T替换传Shape:

~~~java
public static void draw(Shape shape) { /* ... */ }
~~~

桥接方式保持泛型的多态：

假设有以下代码：

~~~java
public class Node<T> {

    public T data;

    public Node(T data) { this.data = data; }

    public void setData(T data) {
        System.out.println("Node.setData");
        this.data = data;
    }
}

public class MyNode extends Node<Integer> {
    public MyNode(Integer data) { super(data); }

    public void setData(Integer data) {
        System.out.println("MyNode.setData");
        super.setData(data);
    }
}
~~~

MyNode 是Node的子类，并且明确的泛型参数类型为Integer。

因为MyNode中的方法setData重载自Node的setData(T data);经过类型擦除后Node中的T用Object替换，由于方法签名不匹配，所以对于MyNode来说有两个方法，即setData(Object data) 和 setData(Integer data);因此MyNode setData方法不会覆盖Node setData方法。为了解决类型擦除后保留泛型类型的多态性，Java编译器会生成一个桥接方法：

~~~java
class MyNode extends Node {

    // Bridge method generated by the compiler
    //
    public void setData(Object data) {
        setData((Integer) data);
    }

    public void setData(Integer data) {
        System.out.println("MyNode.setData");
        super.setData(data);
    }

    // ...
}
~~~
### 泛型的限制：
在使用泛型过程中，我们要注意以下限制
1. 不能使用基本类型去实例化泛型。
    也就是说不能使用int、char等进行替换泛型，但是可以用其包装类型（Integer、Character）进行替换。
2. 不能直接基于泛型参数进行创建对象。
    比如泛型方法中，不能直接通泛型new一个对象，但是可以通过反射机制将泛型的Class作为入参传入，调用其newInstance()方法创建对象。
3. 不能使用泛型定义静态字段。
    类的静态字段是类的所有非静态对象共享的类级变量。因此，不能使用泛型定义静态字段。
4. 不能使用对泛型参数进行强制类型转化或者instanceof
    Java编译器会擦除泛型，因此在运行时无法验证泛型的参数化类型。
5. 不能创建泛型数组
6. 泛型的类型参数不能用在Java异常处理的catch语句中。因为异常处理是由JVM在运行时刻来进行的。由于类型信息被擦除，JVM是无法区分两个异常类型MyException<String>和MyException<Integer>的。对于JVM来说，它们都是 MyException类型的。也就无法执行与异常对应的catch语句。。
7. 无法重载每个重载的形参类型擦除后得到相同原始类型的方法

##  参考资料

- [Java 教程泛型教程](https://docs.oracle.com/javase/tutorial/java/generics/index.html)
- [Java Generics FAQ](http://www.angelikalanger.com/GenericsFAQ/FAQSections/ParameterizedTypes.html)

