# 支持向量分类（SVC) #

参考博文https://blog.csdn.net/u014433413/article/details/78427574

## 支持向量机的优点： ##
- 1、可以解决小样本情况下的机器学习问题。
- 2、可以提高泛化性能。
- 3、可以解决高维问题。
- 4、可以解决非线性问题。
- 5、可以避免神经网络结构选择和局部极小点问题。

##  支持向量机的缺点：
- 1、对缺失数据敏感。
- 2、对非线性问题没有通用解决方案，必须谨慎选择Kernel function来处理。

## 支持向量机的三个层次： ##

线性可分支持向量机、线性支持向量机、非线性支持向量机

- 线性可分支持向量机：训练数据集线性可分，可以有许多直线能把数据正确划分。
- 线性支持向量机：训练数据中有一些特异点(outlier),将这些特异点去除后，剩下大部分的样本点组成的集合是线性可分的。
- 非线性支持向量机：对一组特定的训练数据集，能用<img src="http://chart.googleapis.com/chart?cht=tx&chl=R^{n} "style="border:none;">
中的一个超曲面将正负例正确分开。

## 支撑向量机的主要思想： ##

![](https://raw.githubusercontent.com/frances97/photo1/master/photo3.png)

- 1、SVM是针对线性可分情况进行分析，对于线性不可分的情况，通过使用非线性映射算法将低维输入空间线性不可分的样本转化为高维特征空间使其线性可分，从而使得高维特征空间采用线性算法对样本的非线性特征进行线性分析成为可能。
- 2、SVM基于结构风险最小化理论之上在特征空间中构建最优超平面，使得学习器得到全局最优化，并且在整个样本空间的期望以某个概率满足一定上界。


## 支持向量机的作用：是一种监督式学习的方法主要用于分类（SVC）和回归(SVR)。 ##
本文主要讲解SVC（推导主要是讲解线性可分支持向量机）。

## **SVC的算法的目标** ##
通俗的说是想**寻找一个分割超平面**，使得距离该平面最近的点到该分割平面的**距离最大**。![](https://raw.githubusercontent.com/frances97/photo1/master/photo2.png)

备注：超平面：即图中红色的线，距离支撑线的距离相等；支撑向量：指距离分隔超平面最近的点，即支撑线上的点。

## SVC目标函数 ##

![](https://raw.githubusercontent.com/frances97/photo3/master/photo4.jpg)



**得到了不等式约束问题**：

![](https://raw.githubusercontent.com/frances97/photo3/master/photo5.jpg)


## 目标函数的求解 ##


**步骤1：转化为了拉格朗日乘子法的对偶问题。**

![](https://raw.githubusercontent.com/frances97/photo3/master/photo6.jpg)

其中<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i} \ge{0} "style="border:none;">。此时目标函数变成了

![](https://raw.githubusercontent.com/frances97/photo1/master/photo7.png)

为了方便求解，我们将min和max的位置交换一下：

![](https://raw.githubusercontent.com/frances97/photo1/master/photo8.png)

由于原始优化问题中有不等式条件，这里的对偶问题需要满足下面形式的KKT条件才能有解：
![](https://raw.githubusercontent.com/frances97/photo3/master/photo9.jpg)

**步骤2：<img src="http://chart.googleapis.com/chart?cht=tx&chl=w,b}"style="border:none;">的部分**
![](https://raw.githubusercontent.com/frances97/photo3/master/photo10.jpg)

将上面求得的导数代入拉格朗日方程

![](https://raw.githubusercontent.com/frances97/photo3/master/photo11.jpg)

**步骤3：<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha}"style="border:none;">的部分**

SMO是高效求解这个问题的算法代表。

我们选取两个变量<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i},\alpha_{j}}"style="border:none;">，其它变量保持固定。得到：

![](https://raw.githubusercontent.com/frances97/photo3/master/photo12.jpg)

可以将<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i}}"style="border:none;"> 消掉，只保留<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{j}}"style="border:none;"> ，就变成了关于<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i}}"style="border:none;">的单变量二次规划问题，约束是<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i}\ge{0}}"style="border:none;"> 
，有闭式解。这样算起来肯定快。 那么，怎样找这两个变量<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i},\alpha_{j}}"style="border:none;">比较好呢？

第一个变量<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha__{i}}"style="border:none;">我们肯定选取那个最不满足KKT条件的<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha}"style="border:none;">，第二个我们需要选让目标函数增长得最多的<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha}"style="border:none;">，但每次计算太过麻烦。所以有一个启发式的方法：我们选取与那个<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i}}"style="border:none;">所对应的样本间隔最大的样本所对应的<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha}"style="border:none;">作为<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{j}}"style="border:none;">。这样与更新两个相似的样本相比，目标函数值变化更大。这里的具体推导可以参考 http://blog.csdn.net/ajianyingxiaoqinghan/article/details/73087304 。具体来说，我们可以用**预测值与真实值之差**来衡量这个“样本间的差异”，如果<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha{i}}"style="border:none;">对应的样本预测值与真实值之差为负的，那么我们就尽量找一个差值为正的且绝对值较大的，反之我们就找一个差值为负的且绝对值较大的。在几何上可以理解为找那些暂时被分类错误的样本。 当然，如果得到的<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{j}}"style="border:none;">不能使函数值下降很多，那么我们还可以干脆就暴力找一个让函数值下降最多的<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{j}}"style="border:none;"> ，或者再找一个不符合KKT条件的<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{j}}"style="border:none;">当做第二个<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha}"style="border:none;">。 求解出<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i}^{new}\alpha_{j}^{new}}"style="border:none;">之后，由KKT条件可得，若<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{i}^{new}\g{0}"style="border:none;">，则：
![](https://raw.githubusercontent.com/frances97/photo3/master/photo13.jpg)

就可以对 b 进行更新，更新之后将预测值与真实值之差的列表更新一遍，以供下次循环使用。 当然，如果<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha_{j}^{new}\g{0}"style="border:none;">，也可以计算出相应的<img src="http://chart.googleapis.com/chart?cht=tx&chl=b"style="border:none;">值，如果两个<img src="http://chart.googleapis.com/chart?cht=tx&chl=b"style="border:none;"> 值相等就取该值。 如果不相等就取平均值。 还有一种做法就是，对目前模型中所有的<img src="http://chart.googleapis.com/chart?cht=tx&chl=\alpha\g{0}"style="border:none;"> ，都计算一个<img src="http://chart.googleapis.com/chart?cht=tx&chl=b"style="border:none;">  ，取平均值。 等到算法停止，<img src="http://chart.googleapis.com/chart?cht=tx&chl=b"style="border:none;"> 也计算好了,又因为

![](https://raw.githubusercontent.com/frances97/photo3/master/photo14.jpg)这样，原始优化问题就解完了。


## 核函数 ##

在前面的讨论中，我们假设数据集是线性可分的。但是现实任务中，可能并不存在一个超平面将数据集完美得分开。 
这种情况下，我们可以通过将原始空间映射到一个高维空间，如果高维空间中数据集是线性可分的，那么问题就可以解决了。 
这样，超平面变为：
![](https://raw.githubusercontent.com/frances97/photo3/master/photo15.jpg)
![](https://raw.githubusercontent.com/frances97/photo3/master/photo16.jpg)

## 松弛变量 ##

现实任务中，可能用上核函数还是不能线性可分。或者即使找到线性可分的超平面，也不能判断是不是过拟合。因此，我们将标准放宽一些，允许SVM模型在某些数据点上“出错”，为此，要引入“软间隔”：

![](https://raw.githubusercontent.com/frances97/photo3/master/photo17.jpg)

![](https://raw.githubusercontent.com/frances97/photo3/master/photo18.jpg)

同时，我们希望这个 ξi 尽可能小一点，越小不就越接近前面推导的线性可分么。在目标函数中体现这一点，就得到新的优化问题：

![](https://raw.githubusercontent.com/frances97/photo3/master/photo19.jpg)

![](https://raw.githubusercontent.com/frances97/photo3/master/photo20.jpg)



















