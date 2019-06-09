# Stacking Ensemble Learning #
在一些数据挖掘竞赛中，后期我们需要对多个模型进行融合以提高效果时，常常会用到Bagging，Boosting，Stacking等这几个框架算法。Bagging和Boosting两种方法在本系列的其它课程中已有涉及，这里我们只对Stacking集成学习方法的原理进行讲解。
Stacking Ensemble Learning，一种基于堆叠的集成学习方法，指训练一个模型用于组合其他各个模型。即首先我们先训练多个不同的基模型（或子模型），然后再以基模型的输出作为输入来训练一个高阶模型，以得到一个最终的输出。图1显示了Stacking集成学习的架构，以下我们以分类问题对Stacking的原理进行说明。

![](https://raw.githubusercontent.com/xuwenfeng0459/image/master/11.png)

图1 Stacking 模型架构

如图1所示，Stacking 模型先在整个训练数据集上通过Bootstrapped抽样得到各个训练集合，训练n个不同的基模型（子模型）；获得了基模型后，再以n个基模型的输出组合成一个新的特征向量，如采用concatenate的方式进行组合；最后，再将新的特征向量作为输入，训练另一个高级分类模型。理论上，高阶分类模型可以任何一种分类模型，然而，实际中，我们通常使用单层logistic模型。Stacking集成学习的原理并不复杂，基于以上简单描述我们就可以理解其思想。但是，在实际应用中，Stacking集成学习模型的训练过程通常采用如图2所示的训练方式。
![](https://raw.githubusercontent.com/xuwenfeng0459/image/master/12.png)

图2 Stacking 集成学习的训练过程

如图2所示，上半部分是用一个基础模型进行5折交叉验证，如：用XGBoost作为基础模型Model1，5折交叉验证就是先拿出四折作为training data，另外一折作为testing data。注意：在Stacking中此部分数据会用到整个traing set。例如：假设我们整个training set包含10000行数据，testing set包含2500行数据，那么每一次交叉验证其实就是对training set进行划分，在每一次的交叉验证中training data将会是8000行，testing data是2000行。
每一次的交叉验证包含两个过程，①基于training data训练模型；②基于training data训练生成的模型对testing data进行预测。在整个第一次的交叉验证完成之后我们将会得到关于当前testing data的预测值，这将会是一个一维2000行的数据，记为a1。注意！在这部分操作完成后，我们还要对数据集原来的整个testing set进行预测，这个过程会生成2500个预测值，这部分预测值将会作为下一层模型testing data的一部分，记为b1。因为我们进行的是5折交叉验证，所以以上提及的过程将会进行五次，最终会生成针对testing set数据预测的5列2000行的数据a1、a2、a3、a4、a5，对testing set的预测会是5列2500行数据b1、b2、b3、b4、b5。
在完成对Model1的整个步骤之后，我们可以发现a1、a2、a3、a4、a5其实就是对原来整个training set的预测值，将他们拼凑起来，会形成一个10000行一列的矩阵，记为A。而对于b1、b2、b3、b4、b5这部分数据，我们将各部分相加取平均值，得到一个2500行一列的矩阵，记为B1；在此之后，我们把A作为training data，训练高阶模型，将B 作为testing data测试高阶模型。
以上就是stacking中一个模型的完整流程，Stacking中同一层通常包含多种类型的模型，例如Model2: LR，Model3：RF，Model4: GBDT，Model5：SVM；每种模型的数量也不同。集成学习在机器学习算法中具有较高的准去率，不足之处就是模型的训练过程可能比较复杂，效率不是很高。


