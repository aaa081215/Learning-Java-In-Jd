# **贝叶斯定理与朴素贝叶斯（Naive Bayes）** #
-------------
在日常学习之中，我们经常能见到各种带有“贝叶斯”的词语，例如贝叶斯决策、朴素贝叶斯、贝叶斯估计，有时就会在诸如机器学习或者模式识别的课程上遇到它们中的一两个，学习的时候能把其中某个弄得清清楚楚，时间一长，反而这几个就有些混淆了，因此，集中进行学习整理。


## **1.贝叶斯定理：** ##

**贝叶斯定理**是关于随机事件A和B的条件概率的一则定理。

<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large P(A|B) = \frac{{P(A) \times P(B|A)}}{{P(B)}}\ " style="border:none;">


其中P(A\|B)是指在事件B发生的情况下事件A发生的概率。

在贝叶斯定理中，每个名词都有约定俗成的名称：

-   P(*A*|*B*)是已知B发生后A的 **条件概率** ，也由于得自B的取值而被称作 **A的后验概率**。

-   P(*A*)是A的**先验概率**（或**边缘概率**）。之所以称为"先验"是因为它不考虑任何B方面的因素。

-   P(*B*|*A*)是已知A发生后B的 **条件概率** ，也由于得自A的取值而被称作 **B的后验概率**。

-   P(*B*)是B的**先验概率**或边缘概率。

**例：**

三门问题（Monty Hall problem）亦称为蒙提霍尔问题、蒙特霍问题或蒙提霍尔悖论

问题名字来自该节目的主持人蒙提·霍尔（Monty
Hall）。参赛者会看见三扇关闭了的门，其中一扇的后面有一辆汽车，选中后面有车的那扇门可赢得该汽车，另外两扇门后面则各藏有一只山羊。当参赛者选定了一扇门，但未去开启它的时候，节目主持人开启剩下两扇门的其中一扇，露出其中一只山羊。主持人其后会问参赛者要不要换另一扇仍然关上的门。问题是：换另一扇门会否增加参赛者赢得汽车的概率？

![](https://raw.githubusercontent.com/shengjielai/bigdata_image/master/threegoats.jpg)


1.  最开始的时候，我们对这三扇门之后有什么一无所知，所以我们最好的做法是公平对待三扇门，我们假设An,n=1,2,3为第n个门之后有汽车，那么我们有P(An)=1/3。

2.  假设我们选择门1，主持人打开了门2，这时根据我们打开的门之后是否有汽车，主持人打开的门的概率是会有变化的：如果门1后有汽车，对于一般人（精神正常的人）来说，主持人打开门2和门3的概率基本上应该是一致的，为1/2；如果门2后有汽车，主持人打开门2的概率是0，如果门3后有汽车，主持人打开门2的概率是1。

3.  我们设B为主持人打开了门2，那么我们可以得到：P(B|A1)=1/2，P(B|A2)=0，P(B|A3)=1，也就是2的概率解释。那么我们计算P(A1|B)，这个式子表示我们在得到主持人打开了门2，后面没有汽车这个事实之后，对于P(A1)这个概率的调整：
  
  <img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large P(A|B) = \frac{{P(A) \times P(B|A)}}{{P(B)}} " style="border:none;">

，而P(B)可以通过全概率公式计算：

<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large
 P\left( B \right) = P\left( {B|{A_1}} \right)P\left( {{A_1}} \right) + P\left( {B|{A_2}} \right)P\left( {{A_2}} \right) + P\left( {B|{A_3}} \right)P\left( {{A_3}} \right) = 1/2" style="border:none;">


计算得到P(A1|B)=1/3，这个的含义就是，当我们得到事实B时，我们对先验概率P(An)的值调整为了后验概率P(An|B)。当然如上所见，1门后有汽车的整体概率仍然没有变化，其实变化的是P(A2|B)与P(A3|B)，P(A2)=1/3变成了P(A2|B)=0，P(A3)=1/3变成了P(A3|B)=2/3，提高的概率足够令我们改变自己的决策。

## **2. 朴素贝叶斯（Naive Bayes）** ##

**朴素贝叶斯**的思想基础是这样的：对于给出的待分类项，求解在此项出现的条件下各个类别出现的概率，哪个最大，就认为此待分类项属于哪个类别。

通俗来说，就好比这么个道理，你在街上看到一个黑人，我问你你猜这哥们哪里来的，你十有八九猜非洲。为什么呢？因为黑人中非洲人的比率最高，当然人家也可能是美洲人或亚洲人，但在没有其它可用信息下，我们会选择条件概率最大的类别，这就是朴素贝叶斯的思想基础。

假设某个体有n项特征（Feature），分别为F1、F2、...、Fn。现有m个类别（Category），分别为C1、C2、...、Cm。贝叶斯分类器就是计算出概率最大的那个分类C，由贝叶斯公式可以等价为求下面这个算式的最大值：

<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
{\arg \max }\limits_{C \in \left\{ {{C_1},{C_2},...,{C_m}} \right\}} P\left( {C|{F_1},{F_2},...,{F_n}} \right) 
" style="border:none;">

<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
 = {\arg \max }\limits_{C \in \left\{ {{C_1},{C_2},...,{C_m}} \right\}} \frac{{P\left( {{F_1},{F_2},...,{F_n}|C} \right) \times P(C)}}{{P\left( {{F_1},{F_2},...,{F_n}} \right)}}
" style="border:none;">


由于<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
P\left( {{F_1},{F_2},...,{F_n}} \right) 
" style="border:none;"> 对于所有的类别都是相同的，可以省略，问题就变成了求下式最大值：

<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
 {\arg \max }\limits_{C \in \left\{ {{C_1},{C_2},...,{C_m}} \right\}} P\left( {{F_1},{F_2},...,{F_n}|C} \right) \times P\left( C \right)
" style="border:none;">


由于数据本身的问题，并不容易得到，所以朴素贝叶斯给出一个假设：在给定目标值时属性值之间相互条件独立，从而由独立性可得：


<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
  P\left( {{F_1},{F_2},...,{F_n}|C} \right) \times P\left( C \right) = P\left( {{F_1}|C} \right)P\left( {{F_2}|C} \right)...P\left( {{F_n}|C} \right) \times P\left( C \right)
" style="border:none;">


所以目标转换为找到分类C，使得：

<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
  {\arg \max }\limits_{C \in \left\{ {{C_1},{C_2},...,{C_m}} \right\}} P(C)\prod\limits_i {P({F_i}|C)} 
" style="border:none;">

**朴素贝叶斯分类的过程如下：**

1、设<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
  x = \left\{ {{F_1},{F_2},...,{F_n}} \right\}
" style="border:none;">为一个待分类项，而每个F为x的一个特征属性。

2、有类别集合<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
 C \in \left\{ {{C_1},{C_2},...,{C_m}} \right\}
" style="border:none;">。

3、计算<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
P\left( {{C_1}|x} \right) = P\left( {{C_1}|{F_1},{F_2},...,{F_n}} \right)
" style="border:none;">，<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
P\left( {{C_2}|x} \right) = P\left( {{C_2}|{F_1},{F_2},...,{F_n}} \right)
" style="border:none;"> ，...， <img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
P\left( {{C_m}|x} \right) = P\left( {{C_m}|{F_1},{F_2},...,{F_n}} \right)
" style="border:none;">，
其中每一项的计算都用到上述的贝叶斯公式和独立性假设，简化计算各项
 <img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
P({C_1})\prod\limits_i^n {P({F_i}|{C_1})} ,P({C_2})\prod\limits_i^n {P({F_i}|{C_2})} ,...,P({C_m})\prod\limits_i^n {P({F_i}|{C_m})}
" style="border:none;">
 。

4、如果 <img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
P\left( {{C_j}|x} \right) = \max \left\{ {P\left( {{C_1}|x} \right),P\left( {{C_2}|x} \right),...,P\left( {{C_m}|x} \right)} \right\}
" style="border:none;">，则<img src="http://chart.googleapis.com/chart?cht=tx&chl=\Large 
x \in {C_j}
" style="border:none;">。



**例：****某个医院早上收了六个门诊病人，如下表**

症状　　职业　　　疾病

打喷嚏　护士　　　感冒   
打喷嚏　农夫　　　过敏   
头痛　　建筑工人　脑震荡   
头痛　　建筑工人　感冒   
打喷嚏　教师　　　感冒   
头痛　　教师　　　脑震荡

**现在又来了第七个病人，是一个打喷嚏的建筑工人。请问他患上感冒的概率有多大？**

根据贝叶斯定理：

    P(感冒|打喷嚏x建筑工人) = P(打喷嚏x建筑工人|感冒) x P(感冒) /
    P(打喷嚏x建筑工人)

假定"打喷嚏"和"建筑工人"这两个特征是独立的，因此，上面的等式就变成了

    P(感冒|打喷嚏x建筑工人) = P(打喷嚏|感冒) x P(建筑工人|感冒) x P(感冒) /
    P(打喷嚏) x P(建筑工人)

计算可得：

    P(感冒|打喷嚏x建筑工人) = 0.66 x 0.33 x 0.5 / 0.5 x 0.33 = 0.66

因此，这个打喷嚏的建筑工人，有66%的概率是得了感冒。同理，可以计算这个病人患上过敏或脑震荡的概率。比较这几个概率，就可以知道他最可能得什么病。

这就是贝叶斯分类器的基本方法：在统计资料的基础上，依据某些特征，计算各个类别的概率，从而实现分类。

**朴素贝叶斯分类的优缺点**

优点：

（1）算法逻辑简单,易于实现

（2）分类过程中时空开销小

缺点：

理论上，朴素贝叶斯模型与其他分类方法相比具有最小的误差率。但是实际上并非总是如此，这是因为朴素贝叶斯模型假设属性之间相互独立，这个假设在实际应用中往往是不成立的，在属性个数比较多或者属性之间相关性较大时，分类效果不好。


