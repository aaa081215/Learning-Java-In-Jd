# Java 反射

## 反射机制
在Java中，反射机制通常用于在程序的运行时检查或修改在JVM中运行时行为。例如在运行时环境中，对与任意的类，可以知道该类具有哪些方法及属性，对于任意对象，可以调用它的任意方法。反射机制可以使应用程序执行本来不可能的操作。

Java反射机制主要提供类以下功能：
* 在运行时判断任意一对象的所属的类
* 在运行时构造任意一个类的对象
* 在运行时判断任意一个类所具有的成员变量和方法
* 在运行时调用任意一个对象的方法


Reflection是Java被视为动态（或准动态）语言的一个关键性质。这个机制允许程序在运行时透过Reflection API 取得任何一个**已知名称**的Class的内部信息，包括其modifiers（诸如public、static等等）、superclass（例如 Object）、实现之interfaces（例如Serializable），也包括fields和methods的所有信息，并可于运行时改变fields内容或调用methods。

## 反射 API 简介
Java 主要由以下类来负责实现反射机制：
* Class 类：Instances of the class Class represent
* Field 类：A Field provides information about, and dynamic access to, a single field of a class or an interface.
* Method 类
* Constructor 类
* Array 类

> 除了 Class 类位于 `java.lang`包之外，其余类均在 `java.lang.reflect` 包中


### 示例
```java
   import java.lang.reflect.*;

   public class DumpMethods {
      public static void main(String args[])
      {
         try {
            Class c = Class.forName(args[0]);
            Method m[] = c.getDeclaredMethods();
            for (int i = 0; i < m.length; i++)
            System.out.println(m[i].toString());
         }
         catch (Throwable e) {
            System.err.println(e);
         }
      }
   }
```

执行程序，在命令行中输入：
```
  java DumpMethods java.util.Stack
```

输出结果：
```
public java.lang.Object java.util.Stack.push(java.lang.Object)
public synchronized java.lang.Object java.util.Stack.pop()
public synchronized java.lang.Object java.util.Stack.peek()
public boolean java.util.Stack.empty()
public synchronized int java.util.Stack.search(java.lang.Object)
```

以上，DumpMethods 类演示了反射API的基本作用，程序中我们使用 `Class.forName`来加载指定的类，并调用`getDeclaredMethods`方法来获取类中定义的所有方法列表，然后打印出这个类所具有的方法信息。

## 如何使用反射
反射相关类（例如 Method）均在 `java.lang.reflect`包中。使用这些类必须遵循三个步骤。首先是要获取操作的类的 `Class` 对象。Class 用于表示正在运行的 Java 程序中的类和接口。

获取Class 对象的一种方法是：
Class c = Class.forName("java.lang.String"); //获取String的Class对象
另一种方法为：
Class c = int.class;	//	获取基本类型的类信息
或
Class c = Integer.TYPE; // 包装类型预定义 TYPE字段

其次，调用`getDeclaredMethods`等方法，以获取该类声明的所有方法列表等信息。
最后，就是使用反射API来操作这些信息。

例如：
```
Class c = Class.forName("java.lang.String");    
Method m[] = c.getDeclaredMethods();    System.out.println(m[0].toString());
```
以上示例将打印在String中声明的第一个方法信息。

> Class 类十分特殊。它和一般Java类一样继承自Object，其实体用以表达Java程序运行时的类（classes）和接口（interfaces），也用来表达枚举（enum）、数组（array）、基本类型（boolean、byte、char、short、int、long、float、double）以及关键字void。当一个类被加载，或当类加载器（ClassLoader）的defineClass方法被JVM调用，JVM便自动产生一个Class对象。

### 示例：获取构造方法并创建对象、获取类属性并设置属性值
```java
  import java.lang.reflect.Constructor;
  import java.lang.reflect.Field;
  import java.lang.reflect.Modifier;

  public class ConstructorAndField {

    public static final int J = 37;

    public double e;
    private double d;
    protected int i;
    String s = "testing";

    public ConstructorAndField() {
    }

    public ConstructorAndField(int i, double d) {
        this.i = i;
        this.d = d;

        System.out.println("i = " + i + ", d = " + d);
    }

    public static void main(String args[]) {
        try {
            Class cls = Class.forName("ConstructorAndField");

            // 示例1：获取构造方法
            Constructor ctorlist[] = cls.getDeclaredConstructors();
            for (int i = 0; i < ctorlist.length; i++) {
                Constructor ct = ctorlist[i];
                System.out.println("name = " + ct.getName());
                System.out.println("decl class = " + ct.getDeclaringClass());
                Class pvec[] = ct.getParameterTypes();
                for (int j = 0; j < pvec.length; j++) {
                    System.out.println("param #" + j + " " + pvec[j]);
                }
                Class evec[] = ct.getExceptionTypes();
                for (int j = 0; j < evec.length; j++) {
                    System.out.println("exc #" + j + " " + evec[j]);
                }
                System.out.println("------------");
            }
            System.out.println("------- 示例1 结束 ------------------------------------");

            // 示例2：创建新对象
            Class partypes[] = new Class[2];
            partypes[0] = Integer.TYPE;
            partypes[1] = Double.TYPE;
            Constructor ct = cls.getConstructor(partypes);
            Object arglist[] = new Object[2];
            arglist[0] = new Integer(37);
            arglist[1] = new Double(47.37);
            ConstructorAndField retobj = (ConstructorAndField)ct.newInstance(arglist);
            System.out.println(retobj);
            System.out.println("------- 示例2 结束 ------------------------------------");

            // 示例3：获取类属性
            Field fieldlist[] = cls.getDeclaredFields();
            for (int i = 0; i < fieldlist.length; i++) {
                Field fld = fieldlist[i];
                System.out.println("name = " + fld.getName());
                System.out.println("decl class = " + fld.getDeclaringClass());
                System.out.println("type = " + fld.getType());
                int mod = fld.getModifiers();
                System.out.println("modifiers = " + Modifier.toString(mod));
                System.out.println("-----");
            }
            System.out.println("------- 示例3 结束 ------------------------------------");

            // 示例4：动态修改属性值
            Field fld = cls.getField("e");
            System.out.println("e = " + retobj.e);
            fld.setDouble(retobj, 12.34);
            System.out.println("e = " + retobj.e);
            System.out.println("------- 示例4 结束 ------------------------------------");


            // 示例5：修改私有属性值
            Field fldp = cls.getDeclaredField("d");
            fldp.setAccessible(true);
            System.out.println("d = " + retobj.d);
            fldp.setDouble(retobj, 12.34);
            System.out.println("d = " + retobj.d);
            System.out.println("------- 示例5 结束 ------------------------------------");

        } catch (Throwable e) {
            System.err.println(e);
        }
    }
  }
```
输出结果：
```
  name = ConstructorAndField
  decl class = class ConstructorAndField
  ------------
  name = ConstructorAndField
  decl class = class ConstructorAndField
  param #0 int
  param #1 double
  ------------
  ------- 示例1 结束 ------------------------------------
  i = 37, d = 47.37
  ConstructorAndField@60e53b93
  ------- 示例2 结束 ------------------------------------
  name = J
  decl class = class ConstructorAndField
  type = int
  modifiers = public static final
  -----
  name = e
  decl class = class ConstructorAndField
  type = double
  modifiers = public
  -----
  name = d
  decl class = class ConstructorAndField
  type = double
  modifiers = private
  -----
  name = i
  decl class = class ConstructorAndField
  type = int
  modifiers = protected
  -----
  name = s
  decl class = class ConstructorAndField
  type = class java.lang.String
  modifiers =
  -----
  ------- 示例3 结束 ------------------------------------
  e = 0.0
  e = 12.34
  ------- 示例4 结束 ------------------------------------
  d = 47.37
  d = 12.34
  ------- 示例5 结束 ------------------------------------
```

在上述示例中，我们展示了获取类构造方法，并通过调用带参数的构造方法来创建对象实例，这种方式的好处是它能在运行时以动态的方式查找并调用构造函数，而不是必须要在程序中写死代码。
另外我们还在获取类属性的示例中使用到了`Modifier`，它表示类属性的修饰符，例如：private int。修饰符本身由整数表示，Modifier.toString用于以官方声明顺序返回字符串表示（例如final之前的static）。在该示例中我们还采用`getDeclaredFields`方法来获取仅在类中声明的属性信息，同样，我们也可以采用`getFields`方法获取超类中定义的属性信息。以上示例展示了通过属性名称查找到对应的属性，并赋值，这种方式比较常用。当然，最后一个例子中我们通过设置Field 类的`setAccessible`方法来访问或修改`private`类型的属性，达到了暴力反射私有属性的目的。

### 示例：按方法名称调用方法
```java
  import java.lang.reflect.Method;

  public class Method1 {
      public int add(int a, int b) {
          return a + b;
      }

      public static void main(String args[]) {
          try {
              Class cls = Class.forName("Method1");
              Class partypes[] = new Class[2];
              partypes[0] = Integer.TYPE;
              partypes[1] = Integer.TYPE;
              Method meth = cls.getMethod(
                      "add", partypes);
              Method1 methobj = new Method1();
              Object arglist[] = new Object[2];
              arglist[0] = new Integer(37);
              arglist[1] = new Integer(47);
              Object retobj = meth.invoke(methobj, arglist);
              Integer retval = (Integer) retobj;
              System.out.println(retval.intValue());
          } catch (Throwable e) {
              System.err.println(e);
          }
      }
  }
```

在上述示例中，我们采用`getMethod`方法来查找方法名为`add`，且有两个整型参数的方法。

### 示例：数组
Java语言中的数组是一个特殊类型的类，而且数组引用也可以分配给Object引用。在JDK中，`java.lang.Array`类提供类动态创建和访问元素的各种静态方法。

示例 1:
```java
  import java.lang.reflect.Array;

  public class Array1 {
      public static void main(String args[]) {
          try {
              Class cls = Class.forName("java.lang.String");
              Object arr = Array.newInstance(cls, 10);
              Array.set(arr, 5, "this is a test");
              String s = (String) Array.get(arr, 5);
              System.out.println(s);
          } catch (Throwable e) {
              System.err.println(e);
          }
      }
  }
```

上述示例中，我们创建了一个字符串类型，长度为10 的一维数组，并给数组的第5个元素赋值，最后获取该值并输出结果。


示例2:
```java
  import java.lang.reflect.Array;

  public class Array2 {
   public static void main(String args[]) {
       int dims[] = new int[]{5, 10, 15};
       Object arr = Array.newInstance(Integer.TYPE, dims);

       Object arrobj = Array.get(arr, 3);
       Class cls = arrobj.getClass().getComponentType();
       System.out.println(cls);
       arrobj = Array.get(arrobj, 5);
       Array.setInt(arrobj, 10, 37);

       int arrcast[][][] = (int[][][]) arr;
       System.out.println(arrcast[3][5][10]);
   }
  }
```

上述示例中，我们创建了一个整型的三维数组，并将索引为[3][5][10] 的元素设为 37。

### 示例：范型反射
```java
  import java.lang.reflect.Constructor;
  import java.lang.reflect.Field;
  import java.lang.reflect.Method;
  import java.lang.reflect.ParameterizedType;
  import java.lang.reflect.Type;
  import java.util.HashMap;
  import java.util.List;
  import java.util.Map;

  import static java.util.stream.Collectors.toList;

  public class Generic1 {

      public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException {
          new Bar();

          // 示例：获取类构造方法参数的范型类型
          Constructor<Bar> constructor = Bar.class.getConstructor(List.class);
          Type[] constructorGenericParameterTypes = constructor.getGenericParameterTypes();
          Type type = ((ParameterizedType) (constructorGenericParameterTypes[0])).getActualTypeArguments()[0];
          System.out.println("constructorGenericParameterType = " + type);

          // 示例：获取类成员变量的范型类型
          Field field = Bar.class.getDeclaredField("map");
          field.setAccessible(true);
          Type fieldGenericType = field.getGenericType();
          if (fieldGenericType instanceof ParameterizedType) {
              ParameterizedType parameterizedType = (ParameterizedType) fieldGenericType;
              System.out.println("fieldGenericType1 = " + parameterizedType.getActualTypeArguments()[0]);
              System.out.println("fieldGenericType2 = " + parameterizedType.getActualTypeArguments()[1]);
          }

          // 示例：获取类成员方法的参数的范型类型
          Method method = Bar.class.getDeclaredMethod("convert", List.class);
          Type[] methodGenericParameterTypes = method.getGenericParameterTypes();
          type = ((ParameterizedType) (methodGenericParameterTypes[0])).getActualTypeArguments()[0];
          System.out.println("methodGenericParameterType = " + type);

          // 示例：获取类成员方法的返回值的范型类型
          Type methodGenericReturnType = method.getGenericReturnType();
          type = ((ParameterizedType) (methodGenericReturnType)).getActualTypeArguments()[0];
          System.out.println("methodGenericReturnType = " + type);
      }

  }


  class Foo<T> {

      private T[] ts;

      public Foo() {
          //示例：获取父类泛型的具体类型
          Type genericSuperclass = getClass().getGenericSuperclass();
          Type params = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];

          System.out.println("genericSuperclass = " + params);
      }

  }

  class Bar extends Foo<String> {

      private Map<String, Integer> map = new HashMap<>();
      List<Integer> list;

      public Bar() {
      }

      public Bar(List<Integer> list) {
          this.list = list;
      }

      public List<Integer> convert(List<String> stringList) {
          return stringList.stream().map(t -> Integer.valueOf(t)).collect(toList());
      }
  }
```

输出结果：
```
  genericSuperclass = class java.lang.String
  constructorGenericParameterType = class java.lang.Integer
  fieldGenericType1 = class java.lang.String
  fieldGenericType2 = class java.lang.Integer
  methodGenericParameterType = class java.lang.String
  methodGenericReturnType = class java.lang.Integer
```

上述示例中，我们使用了`Type`接口，它只有一个实现类`Class`，但是它有另外四个子接口，分别是：
* GenericArrayType，该接口表示一种数组类型，其组件类型为参数化类型或类型变量，示例中`Bar`类的成员变量`map`即为参数化类型数组`Map<String, String>[] map`，`Foo`中的成员变量`ts`即为类型变量数组`T[] ts`
* ParameterizedType，该接口表示参数化类型，如示例中`List<Integer>`
* TypeVariable，该接口是类型变量的通用超接口，可以表示泛型声明的参数类型，例如Foo<T>
* WildcardType，表示一个通配符类型表达式，如 `?`、`? extends Number` 或 `? super Integer`

以上4个子接口描述了范型的四种形式。

----
## 参考
1. Using Java Reflection, https://www.oracle.com/technetwork/articles/java/javareflection-1536171.html

2. Dynamic Proxy Classes, https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html
