# JAVA内存模型拨乱反正
> 作为一名JAVA研发，日常开发中遇到压测、性能、线上问题都免不了与JVM打交道，我们最常提到的：“GC了”，“垃圾回收爱频繁了”，“堆太小了”，“FullGC太频繁了”，“内存不够了”。
> 
> JAVA最为被使用最广泛的编程语言之一，在互联网上能够查到海量的相关资料，这些资料里边不乏有大牛关于JVM的经典文章。JVM是一个非常庞大的体系，这就导致很多人对JVM的理解很容易发生管中窥豹的现象，因此网上也会存在很多文章是研发人员自己的体会和理解，有些理解很到位，有些又有些有失偏颇。这就导致我们在查找一些JVM相关资料的时候，会造成一定的误解。

## 本文的依据
网上的JVM相关文章不计其数，本文如何做到没有“管中窥豹”？我们的原则是所有信息基于官方文档、官方研发人员、国内外知名JVM专家的博客和文章。
笔者认知能力有限，虽然不能做到百分百完全正确，但是每一点都可以做到有据可依。


## JVM家族和外戚
我们平时开发基本上都是基于JDK开发，因此在很多研发人印象中，什么JAVA虚拟机、JVM就是我们现在用的这个从Oracle官网上下载的JDK里边自带的这个组件，我们叫做HotSpot VM。实际上在整个JAVA社区，有非常多的JVM，只是是我们接触最多的虚拟机。下面我们简单介绍下各大厂商虚拟机的历史和区别，大家就能对JVM有一个初步的全局印象。


### HotSpot VM/JRockit VM/IBM J9 VM

首先介绍当年使用最广泛的的三大JVM，原创公司分别来自SUN、BEA、IBM（冷知识：IBM、微软、RedHat是JAVA社区非常大的贡献和推动者，当年JAVA推出的applet客户端程序就是微软优先在IE浏览器上支持的）。目前，SUN公司和BEA公司被Oracle公司收购，所以现在叫Oracle HotSpot和Oracle JRockit。

IBM J9是IBM公司开发的商业JVM，广泛用于其商用应用服务器Websphere，也是性能非常优秀的商用JVM+应用服务器组合，J9是内部代号，正式名称是“IBM Technology for Java Virtual Machine”，但是J9的流行范围更广，J9的名字就延续了下来。近两年JDK发布节奏的“无限期”延迟，都是基于模块化之争，这里我个人认为就是受J9高度模块化优势的影响。

Oracle HotSpot随官方JDK发布，由于其“开源性”（这里的开源指的是openJDK，细节后边详述）和JDK的广泛使用，HotSpot是广大JAVA程序员最多接触的虚拟机。（冷知识：HotSpot并不是SUN公司初创的，1996年SUN公司发布第一版JDK，所带的虚拟机叫做Classic VM，随之发布的《JAVA虚拟机规范》规定了JAVA虚拟机的开发规范，使得任意公司都能按照规范实现自己的JVM虚拟机，因此一家叫做Longview的公司开发了HotSpot，后由于其出色的性能表现，被SUN公司收购。这里也要感激SUN公司有如此的胸襟，才有了今天JAVA的蓬勃发展。）

Oracle JRockit作为当年BEA最佳套件组合（weblogic+JRockit），与HotSpot类似，原创公司Appeal Virtual Machines，随weblogic一起发布，weblogic是BEA公司非常优秀的商用应用服务器（与之对应的是免费开源的Tomcat）。

BEA最为商业公司发布的weblogic+JRockit组合，在性能和技术支持等多方面超越HotSpot+Tomcat组合，但是由于互联网公司的兴起，HotSpot+Tomcat的开源免费性成为了互联网公司的首选，并且随着技术人员不断的发展和社区贡献，HotSpot+Tomcat组合的性能等各个方面已经非常强悍，足以承担618、双十一的流量。

近两年Oracle也承诺并一直在推进Oracle HotSpot和Oracle JRockit的整合[1]。

### Apache Harmony/Google Android Dalvik VM

准确来说，Harmony和Dalvik都不算是JVM，它们叫做某某虚拟机，但是他们又有千丝万缕的联系。历史关系错综负责，大家可以从谷歌搜索到他们的历史渊源，这里不赘述。简单来说，Harmony是JAVA社区各大厂商（主要是IBM和Intel）和SUN之间的开源协议口水仗中诞生的，以下引用摘自网络，各位看官自行甄别：

>"想借此获得 Java世界的话语权. IBM利用 Harmony来攻击Sun 的不开源,结果 Sun把Java 在GPL协议下开源之后 ,IBM又攻击SUN 把Java "不负责任地开源 "---只有Harmony 用Apache License协议才是合适的"

但是Harmony却迟迟不能通过JCP的TCK认证，因此它就不能生成自己“兼容JAVA语言”，也就是说不能在官方被JAVA社区认可，除非自己能打出一片天地，否则开发者不会大规模的专门为Harmony去开发。

Dalvik就能做到“自己的一片天地”。

网络上有一种说法：

>"Harmony在Java 时间里一直没取得名分 ,独守空房地时候,被 Google看中, 作为其手机操作系统 Android里的虚拟机"

实际上这是不准确的，Harmony虚拟机理论上来说是遵照JAVA虚拟机规范来开发的，只不过没有获得JCP的认可，而Dalvik从开始就没有遵照JAVA虚拟机规范来开发，它甚至不能执行class文件。之所以有Harmony成就了Android的说法，是因为

>"其中有少量部分跟Harmony能扯上关系：
>
>* platform/dalvik/vm/native：由于VM与core libraries有很小一部分高度耦合（例如java.lang.Object、java.lang.String等类），既然Dalvik VM与Harmony的类库搭配使用，这部分就不得不与Harmony耦合。但这部分代码却不是源自Harmony，而是Android自己新写的。
>* platform/dalvik/vm/compiler/codegen/x86/libenc/：这是比较新的Dalvik VM中x86平台上的JIT所依赖的libenc库。该库直接取自Apache Harmony。但这是比较后来的事情了，早期Android里并没有这个。
>
>* 其它部分都跟Harmony没有关系。"

### 另外还有很多优秀的开源和商用JVM，这里就不细说了，不过两个小八卦，大家自行娱乐。

> 史上最贵的9行代码--Android和JAVA扯不清剪不断的关系，告诉我们专利和知识产权的重要性。

> SUN与微软的侵犯商标诉讼案--一场改变JAVA社区格局的案件（评论详见：《深入理解Java虚拟机》）


## JDK、JRE、JVM、JAVA API
 JDK : 全称Java Development ToolKit(Java开发工具包)。它包含Java基础的类库，Java运行环境，Java开发运行工具（比如javac、jmap等）。

 JRE:全称Java  Runtime  Enviromental，即JDK中包含的java运行环境。所有的Java程序都要在JRE下运行。JRE包含JAVA API和JVM等运行class文件所必须的组件。

JAVA API：即JAVA核心类库，比如我们平时用到的String、ArrayList这些类所在的库（rt.jar）。因为新的JAVA API都是随JDK一起发布，所以有时我们会在一定的语境下，用JDK来代指Java API。比如：

> "你用的是哪个版本的JDK？JDK5以后就支持泛型了。"

 JVM：全称Java Virtual Mechinal(JAVA虚拟机)。JVM是JRE的一部分，是class文件运行必须的组件。我们经常提到的垃圾回收器、java内存都是对JVM的设置。

##OpenJDK与OracleJDK
以下引自RednaxelaFX：（引自2015年知乎）

从代码完整性来说，
Sun JDK > SCSL > JRL > OpenJDK

Sun JDK有少量代码是完全不开发的，即便在SCSL版里也没有。但这种代码非常非常少。

SCSL代码比JRL多一些closed目录里的内容。

JRL比OpenJDK多一些受license影响而无法以GPLv2开放的内容。


但从Oracle JDK7 / OpenJDK7开始，闭源和开源版的实质差异实在是非常非常小。与其说OpenJDK7是“不完整的JDK”，还不如说Oracle JDK7在OpenJDK7的基础上带了一些value-add，其中很多还没啥用（例如browser plugin）。

## 串行/并行/并发
串行：一个线程独占所有时间片，一个时间段内，只有一个线程在执行。

并行：多个线程，频繁切换时间片，由于时间片很短，对外部用户来说，一个时间段内，是多个线程在运行，实际上计算资源是不变的。并行的好处是，当某个线程在等待外部资源的时候，实际上是不需要计算资源的，这时候可以把计算资源让出给其他线程，从而达到资源被充分利用的效果。（如果多个线程，没有等待资源等阻塞情况，多个线程并行执行并不一定比多个线程串行执行要快，原因是线程上下切换同样会消耗计算资源）。但是在同一个时间点，不可能存在两个线程同时执行，这是并行和并发的根本区别。

并发：真正的同一个时间点存在多个线程同时执行。例如我们现在的多核CPU，可以支持真正的并发。多个线程分别分配不同核的同一个时间点的时间片。

## 内存溢出与内存泄露
内存溢出：我们可以理解为，一个杯子，容量是固定的，杯子慢了，水就溢出了，真正的内存溢出，解决方式很简单，就像是换一个大杯子就可以了，把内存调大，或者把垃圾对象回收掉，就能解决内存溢出的问题。

内存泄露：内存泄露是真正的内存黑洞，就是对象不知道跑到哪里去了，它永远存在于内存中，系统却无法处理它。

## 分代模型、永久代、运行时常量池
这里一定要明确，不是所有的JVM的内存管理模型都是分代的，分代模型是由垃圾回收算法决定的。目前我们最常用的HotSpot，在G1垃圾回收器之前，是根据对象存活时间将对象分类分区，对于不同类型的对象分区，采用不同的垃圾回收算法，达到最优的垃圾回收性能。

永久代我们可以理解为方法区，存放的是类信息、常量、静态变量等。方法区和永久代是两个命名维度，方法区指的是存放类信息的内存区域，永久代是从垃圾回收维度定义的分代名称。实际上JAVA虚拟机规范中并没有规范方法区的细节实现方式，所以J9、JRocket是没有永久代概念的。HotSpot JDK7开始也已经把字符串常量池从永久代移走。

## 复制算法

关于复制算法的一点说明：

标准的复制算法是将内存分为两块，用于转移存活的对象。实际上HotSpot的分代模型中，应用复制算法的年轻代却分成了三个区域：eden、s0、s1。

这个设计的理论基础是：IBM的研究结果，“98%的对象都是朝生夕灭的”（注：IBM研究结果出来以前，HotSpot就是这么布局的，这里只是说明一下理论基础）。因此，我们可以认为只有2%的对象需要“转移”，因此目标转移空间并不需要很大，每次转移后，将survivor区与eden区合并，另一块survivor备用转移。

## -Xms和-Xmx为什么一定要设置一致？

首先这个是基于官方的建议：
> -Xms and -Xmx are often set to the same value [[注:oracle doc]]("https://docs.oracle.com/javase/8/docs/technotes/tools/windows/java.html#BABHDABI")

这里并没有解释原因，网上最常见的说法是，频繁的申请缩减内存空间，影响系统性能。

这个结论没有数据支撑，不能说明申请、缩减内存空间会对系统造成影响。（性能指标待补充，TODO）

一个更有说服力的原因，是JVM内存空间不足的时候，会首先触发GC，GC是非常消耗性能的，所以这个理由比较有说服力。（扩容之前是不是真的要触发GC，这个需要充分的证据，不能看到扩容之前GC了，就下结论说扩容之前一定会GC。TODO）


## 响应优先/吞吐优先

这两个是从垃圾回收器选择策略来说的，具体来说，CMS垃圾回收属于响应优先垃圾回收器，PS垃圾回收属于吞吐优先垃圾回收器。区别在于：响应优先在于尽可能客户端对GC的感知，即尽可能降低GC的STW时长；吞吐优先不关注GC的停顿时间，关注点在于单位时间内最大限度输出计算能力。

## JDK默认的老年代垃圾回收器
这里对[《深入理解Java虚拟机：JVM高级特性与最佳实践（第2版）》]("https://item.jd.com/11252778.html")一个勘误：
>老年代GC（Major GC/Full GC）：指发生在老年代的GC，出现了Major GC，经常会伴随至少一次的Minor GC（但非绝对的，在Parallel Scavenge收集器的收集策略里就有直接进行Major GC的策略选择过程）。Major GC的速度一般会比Minor GC慢10倍以上。

深入理解Java虚拟机这本书，由于其通俗性，成为了大部分java程序员的JVM入门数据，它在国内对JVM的推动做出的贡献非同小可。因此一些观点在java程序员之间就传播沉淀下来，这里提到的一点一定要重点指出：

我们平时所说的：CMS、Serial-old、PS这些所谓的老年代垃圾回收器，都是指的Major GC/Full GC，而不是单单回收老年代。只有CMS的concurrent collection会并发的触发old GC，除此之外没有单独对老年代的回收，这个从垃圾回收日志就能看出，Full GC的时候young、old、perm区都会被回收，而没有所谓的old回收日志。[[注:勘误]]("https://book.douban.com/people/RednaxelaFX/annotation/24722612/")


垃圾回收只存在4中形式：

Young GC：只收集young gen的GC；

Old GC：只有CMS的concurrent collection是这个模式；

Mix GC：收集整个young gen以及部分old gen， G1特有；

Full GC：收集整个堆，包括young、old、perm（如存在）等所有部分；

## 我们的程序多数都是为了保证用户体验，为什么不使用响应优先的CMS，而是使用的PS？
我们的线上系统，如果没有主动设置过，默认的GC配置是这样的： PS MarkSweep + PS Scavenge

为什么我们不能设置成parNew+CMS，响应优先的模式更适合我们的使用场景呢。

原因有二：

* 我们的线上JVM配置是默认的，我们一般不会主动配置JVM参数。 PS MarkSweep + PS Scavenge就是默认配置。

* CMS对CPU敏感，CMS由于采用标记-清除算法，因此会产生碎片，从而会更频繁触发GC，CMS垃圾回收与用户线程是并发执行的，因此会产生浮动垃圾。这些原因导致一个问题就是，CMS需要更多的内存空间和CPU资源。因此如果JVM设置的Xmx<3G，会导致可用的老年代非常少，从而频繁的GC，不能保证系统性能。（参考：毕玄-为什么不考虑cms）



**参考：**

[《深入理解Java虚拟机：JVM高级特性与最佳实践（第2版）》]("https://item.jd.com/11252778.html")

[https://community.oracle.com/community/java]("https://community.oracle.com/community/java")

[https://en.wikipedia.org/wiki/Java_virtual_machine]("https://en.wikipedia.org/wiki/Java_virtual_machine")

[https://zh.wikipedia.org/wiki/JRockit]("https://zh.wikipedia.org/wiki/JRockit")

[https://zh.wikipedia.org/wiki/Apache_Harmony]("https://zh.wikipedia.org/wiki/Apache_Harmony")

[1][Mark Reinhold Talks About JRockit/Hotspot Integration]("http://www.infoq.com/news/2010/02/jrockit_hotspot")

