# 深入理解Java序列化和反序列化机制
> 本文带大家理解什么是Java序列化和反序列化，如何使用和实现原理

## 什么是序列化
    序列化:把Java对象保存为二进制字节码的过程
    反序列化:把二进制码重新转换成Java对象的过程
    序列化真正要保存的是对象属性的类型，和属性的值


## 使用场景：
    分布式系统，对象在网络上传输，典型场景:JSF
    服务器钝化，对象持久化

## Java序列化实现：
    实现java.io.Serializable 接口的类才允许做序列化，底层实现：Java对象 instanceof Serializable 来判断
    使用对象流

~~~java

import java.io.Serializable;
public class Joy implements Serializable {
private static final long serialVersionUID = 1L;

private static int age=30;
private String name;
transient private String car;
private String color;

public static int getAge() {
return age;
}
public static void setAge(int age) {
Joy.age = age;
}
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
public String getColor() {
return color;
}
public void setColor(String color) {
this.color = color;
}
public String getCar() {
return car;
}
public void setCar(String car) {
this.car = car;
}
@Override
public String toString() {
return "static age:"+age+"\tname:"+name+"\t transient car:"+car+"\tcolor:"+color;
}
}

/**
* 序列化IOException
*/
private static void serializeJoy() throws Exception {
Joy Joy = new Joy();
Joy.setColor("black");
Joy.setName("naruto");
Joy.setCar("0000");
System.out.println(Joy.toString());
// ObjectOutputStream 对象输出流，将 Joy 对象存储到E盘的 Joy.txt 文件中，完成对 Joy 对象的序列化操作
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("d:/java_demo/Joy.txt")));
oos.writeObject(Joy);
System.out.println("Joy 对象序列化成功！");
oos.close();
}


/**
* 反序列化
*/
private static Joy deserializeJoy() throws Exception {
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("d:/java_demo/Joy.txt")));
Joy person = (Joy) ois.readObject();
System.out.println("Joy 对象反序列化成功！");
return person;
}

~~~

    结果：

    static age:50 name:naruto transient car:0000 color:black
    Joy 对象序列化成功！
    Joy 对象反序列化成功！
    static age:50 name:naruto transient car:null color:black

## 对象序列化的文件格式：
    对象序列化是以特殊的文件格式存储对象数据的，当存储一个对象时，这个对象所属的类也必须存储。这个类的描述包含：

    1.类名
    2.序列化版本的唯一的ID serialVersionUID
    3.描述序列化方法的标志集
    4.对数据域的描述


    Joy.txt

    aced 0005         73         72         0008      6465 6d6f 2e4a 6f79
    魔法数+版本号  表示新对象  类描述开始  描述长度   字符串"demo.Joy"

    0000 0000 0000 0001  02                  00 02          4c                 0005       636f 6c6f 72
        suid1L          实现了Serializable  成员变量个数 第一个成员变量L对象  成员名长度5 成员名：color

    74                     0012                   4c6a 6176 612f 6c61 6e67 2f53 7472 696e 673b 
    表示该类型为String    String类型描述的长度    String类描述字符串"LJava/lang/String"

           4c             00 04      6e 616d 65   71             07e 0001
    第二个成员变量  成员名长度  成员名：name  表示类型为引用  引用的第一个成员变量String 

        78         70           74                  00 05  62 6c61 636b         74             0006    6e 6172 7574 6f
    类描述结束  没有父类结束  第一个成员变量String  长度5   值：black         第二个成员变量   长度6   值：naruto


## 对象序列化算法：
    1.对你遇到的每一个对象引用都关联一个序列号
    2.对于每一个对象，当第一次遇到时，保存其对象数据到流中。
    3.如果某个对象之前已经保存过，那么在写的时候，只会写出与之前保存过的序列号相同的对象；
    4.对于流中的对象，在第一次遇到其序列号时，构建它，并使用流中数据来初始化它。然后记录这个序列号和新对象之间的关联；
    5.当遇到与之前保存过的序列号的对象相同时，获取这个顺序号相关联的对象引用；


## 补充：
    transient关键字：
    如果我们不希望某些数据被序列化，可以使用transient关键字。他们会在对象序列化时被跳过





