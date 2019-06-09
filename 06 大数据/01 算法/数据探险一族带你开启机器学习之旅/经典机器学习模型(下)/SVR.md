# 支持向量回归（SVR）#

参考文章：https://zhuanlan.zhihu.com/p/33692660

## SVR简单介绍： ##
SVR全称是support vector regression，是SVM（支持向量机support vector machine）对回归问题的一种运用。

我们知道，最简单的线性回归模型是要找出一条曲线使得残差最小。同样的，SVR也是要找出一个超平面，使得所有数据到这个超平面的距离最小。

![](https://raw.githubusercontent.com/frances97/photo3/master/photo21.jpg)

## SVR与SVC的异同 ##

不同的是：在SVC中我们要找出一个间隔（gap）最大的超平面，而在SVR，我们定义一个ε，如上图所示，定义虚线内区域的数据点的残差为0，而虚线区域外的数据点（支持向量）到虚线的边界的距离为残差（ζ）。与线性模型类似，我们希望这些残差（ζ）最小。所以大致上来说，SVR就是要找出一个最佳的条状区域（2ε宽度），再对区域外的点进行回归。

![](https://raw.githubusercontent.com/frances97/photo3/master/photo22.jpg)


对于非线性的模型，与SVM一样使用核函数（kernel function）映射到特征空间，然后再进行回归。

其余推导和思想类似svc。