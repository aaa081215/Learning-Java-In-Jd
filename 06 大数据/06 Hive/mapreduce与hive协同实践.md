# mapreduce与hive协同实践

## Hadoop简介

&ensp;&ensp;&ensp;&ensp; Hadoop是一个由Apache基金会所开发的可靠且可扩展的分布式系统基础架构，其最核心的设计就是：HDFS，YARN（第二代MapReduce）。HDFS用于存储海量数据，YARN出现于Hadoop 2，意味着Hadoop有了新的处理模型。YARN是一个集群资源管理系统，允许任何一个分布式程序(不仅仅是MapReduce)基于Hadoop集群的数据而运行。

&ensp;&ensp;&ensp;&ensp; 离线大数据的最常用的工具是Hive，对应的语言是HiveQL，其底层将HiveQL转化为Java MapReduce。MapReduce是一种编程模型，该模型基于HDFS存储的海量数据，将原始数据转换为对一个键-值对数据集的计算。

&ensp;&ensp;&ensp;&ensp; 下面逐一介绍MapReduce的工作机制，开发流程，以及MapReduce的一种设计模式：辅助排序，最后再进入主题，提供一个实际工作中遇到的例子，HiveQL似乎解决不了此类问题。

## map和reduce

&ensp;&ensp;&ensp;&ensp; MapReduce任务过程分为两个处理阶段：map阶段和reduce阶段。每阶段都以键-值对作为输入和输出，其类型由程序员来选择，或者使用定制类型。程序员还需要写两个函数：map函数和reduce函数(只有这两部分提供对外接口，可以使用非Java代码，Hadoop Streaming使用Unix标准流作为Hadoop和应用程序之间的接口，所以我们可以使用任何编程语言通过标准输入/输出来写MapReduce程序)，以及用于运行的驱动类。

map: (K1, V1) → list(K2, V2)

reduce: (K2, list(V2)) → list(K3, V3)

为了全面了解MapReduce，考虑这样一个实际问题：求解学生各学科成绩的最高分。

考虑以下输入示例数据(其中科目代码和成绩用制表符分隔，这个考试有点特别，答错题会扣分，因此会有负的成绩！)：

科目代码  成绩

```
1950  111
1950  78
1949  0
1949  22
1949  -11
```

这些行以键-值对的方式作为map函数的输入，此步骤由MapReduce框架完成：

```
(  0, 1950  111)
(106, 1950  78)
(212, 1949  0)
(318, 1949  22)
(424, 1949  -11)
```

map函数的输出，获取map输入的值将其处理后输出

```
(1950,  111)
(1950,  78)
(1949,  0)
(1949,  22)
(1949,  -11)
```

ruduce函数的输入：

```
(1950,  [111, 78])
(1949,  [0, 22, -11])
```

ruduce函数的输出，现在要做的是遍历整个列表，并从中找出最高成绩

```
(1950,  111)
(1949,  22)
```

这便是最终输出结果。

整个数据流类似图2-1。在图的底部是Unix管线，用于模拟整个MapReduce的流程，其中的sort和shuffle由框架完成，数据在到达ruduce前按照键进行排序/分区/分组。
 
图2-1 MapReduce的逻辑数据流

![](06 大数据/06 Hive/图2-1.png)

## 开发Java MapReduce

### 开发工具准备

首先得安装JDK，Idea/Eclipse(以便于使用提示功能)，并下载一个hadoop-x.x.x.tar.gz文件，例如下载hadoop-3.1.0.tar.gz，解压后需要以下三个jar文件：
```
hadoop-common-x.x.x.jar
commons-cli-x.x.jar
hadoop-mapreduce-client-core-x.x.x.jar
```
在Idea中导入这三个jar包，就能查看Hadoop相关接口了
 
### Java MapReduce代码

// 求解学生各学科成绩的最高分

```
package com.demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;


public class MaxScore {


    public static class MaxScoreMapper
            extends Mapper<LongWritable, Text, Text, IntWritable> {


        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String[] line = value.toString().split("\t");
            context.write(new Text(line[0]), new IntWritable(Integer.parseInt(line[1])));
        }
    }

    public static class MaxScoreReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        @Override
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context)
                throws IOException, InterruptedException {

            int maxScore = Integer.MIN_VALUE;
            for (IntWritable value : values) {
                maxScore = Math.max(maxScore, value.get());
            }
            context.write(key, new IntWritable(maxScore));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MaxScore <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Max Score");

        job.setJarByClass(MaxScore.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(MaxScoreMapper.class);
        job.setReducerClass(MaxScoreReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

### 编译打包运行

编译Java MapRuduce需要三个jar包，如何编译视个人喜好而定，这里给出一个在Linux环境下编译打包的例子，直接用javac进行编译，咱不需要使用maven等其它工具。当然也可以在Idea或者eclipse导出jar包。
```
hadoop-common-3.1.0.jar
commons-cli-1.2.jar
hadoop-mapreduce-client-core-3.1.0.jar
```
compile_java.sh用于编译Java源文件，首先确保在Linux环境下已经安装了JDK，Hadoop，并且配置了HADOOP_HOME环境变量，它指向Hadoop的安装目录。

// compile_java.sh 
```
#!/bin/bash

jars=$HADOOP_HOME/share/hadoop/common/hadoop-common-3.1.0.jar:\
$HADOOP_HOME/share/hadoop/common/lib/commons-cli-1.2.jar:\
$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-client-core-3.1.0.jar
#也可以使用以下代码遍历所有jar文件
#for jar in $HADOOP_HOME/share/hadoop/common/lib/*
#do
# jars=$jars:$jar
#done

javac -classpath $jars $1 -d .
```

执行compile_java.sh，Java源文件作为参数，编译：

sh compile_java.sh MaxScore.java

本例中包名是com.demo，因此会生成以下目录结构，以及文件：

![](06 大数据/06 Hive/编译.png)

打包：

jar cvf ms.jar com

![](06 大数据/06 Hive/打包.png)
 
查看ms.jar目录结构：

jar tf ms.jar

![](06 大数据/06 Hive/查看目录结构.png)

 
输入文件：filescore

![](06 大数据/06 Hive/输入文件.png)

运行：

hadoop jar ms.jar com.demo.MaxScore filescore out
 
查看结果：

hadoop fs -cat out/part-r-00000

![](06 大数据/06 Hive/查看结果.png)

出现 completed successfully 表示已经成功了，此时会生成一个空文件_SUCCESS，输出结果以part-r-开头，有多少个reduce就有多少个part-r-文件，默认一个reduce。实际工作中我们会建一个Hive外表表来保存Map Reduce的输出结果。

至此，一个完整的Java MapReduce开发流程演示完毕。

## 辅助排序

&ensp;&ensp;&ensp;&ensp; 该示例用辅助排序求解学生各学科成绩的最高分，主要演示辅助排序的工作机制，首先，自定义一个由学科代码(主键)和成绩(次键)组成的组合键IntPair，MapReduce框架在记录到达reducer之前默认按键对记录排序/分区/分组. 而这里我们在分区和分组时均只考虑主键(学科代码)，这样同一学科的记录会被全部送到同一个reducer，而排序是先按主键正序排序，再按次键倒序排序，最后在Reducer中只需输出第一条即可获得各学科的最高成绩.

map: (K1, V1) → list(K2, V2)

partition对中间结果键-值对(K2, V2)进行处理，并返回一个分区索引，值被忽略.

partition: (K2, V2) → integer

reduce: (K2, list(V2)) → list(K3, V3)

### Java代码

// 用辅助排序 求解学生各学科成绩的最高分

```
package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MaxValueUsingSecondarySort
    extends Configured implements Tool {


  public static class MaxValueMapper
      extends Mapper<LongWritable, Text, IntPair, NullWritable> {

    @Override
    protected void map(LongWritable key, Text value,
              Context context) throws IOException, InterruptedException {
      String[] lines = value.toString().split("\t");
// lines[0] 为学科代码, lines[1]为成绩
      context.write(new IntPair(Integer.parseInt(lines[0]), Integer.parseInt(lines[1])), NullWritable.get());

    }
  }

  public static class MaxValueReducer
      extends Reducer<IntPair, NullWritable, IntPair, NullWritable> {

    @Override
    protected void reduce(IntPair key, Iterable<NullWritable> values,
               Context context) throws IOException, InterruptedException {

// 直接输出第一条即可获得最高分, 由于成绩可以直接由组合键获得, 
// 不需要在值上附加信息, 所以用NullWritable
      context.write(key, NullWritable.get());/*]*/

    }
  }

  public static class FirstPartitioner
      extends Partitioner<IntPair, NullWritable> {

    @Override
    public int getPartition(IntPair key, NullWritable value, int numPartitions) {
      // 
      return Math.abs(key.getFirst() * 127) % numPartitions;
    }
  }

  public static class KeyComparator extends WritableComparator {
    protected KeyComparator() {
      super(IntPair.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
      IntPair ip1 = (IntPair) w1;
      IntPair ip2 = (IntPair) w2;
      int cmp = IntPair.compare(ip1.getFirst(), ip2.getFirst());
      if (cmp != 0) {
        return cmp;
      }
      return -IntPair.compare(ip1.getSecond(), ip2.getSecond()); //倒序
    }
  }

  public static class GroupComparator extends WritableComparator {
    protected GroupComparator() {
      super(IntPair.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
      IntPair ip1 = (IntPair) w1;
      IntPair ip2 = (IntPair) w2;
      return IntPair.compare(ip1.getFirst(), ip2.getFirst());
    }
  }

  @Override
  public int run(String[] args) throws Exception {

    if (args.length < 3) {
      System.err.printf("Usage: %s [generic options] <input> <output> <NumReduceTasks>\\n", getClass().getSimpleName());
      ToolRunner.printGenericCommandUsage(System.err);
      return -1;
    }
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "MaxValueUsingSecondarySort");


    job.setJarByClass(getClass());
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    job.setMapperClass(MaxValueMapper.class);

  // 按学科代码进行分区
    job.setPartitionerClass(FirstPartitioner.class);
  // 按照学科代码和成绩(降序)排序键
    job.setSortComparatorClass(KeyComparator.class);
  //按学科代码进行分组
    job.setGroupingComparatorClass(GroupComparator.class);

  // 设置 reduce个数
    job.setNumReduceTasks(Integer.parseInt(args[2]));
    job.setReducerClass(MaxValueReducer.class);
    job.setOutputKeyClass(IntPair.class);
    job.setOutputValueClass(NullWritable.class);

    return job.waitForCompletion(true) ? 0 : 1;
  }

  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new MaxValueUsingSecondarySort(), args);
    System.exit(exitCode);
  }


  // 自定义由学科代码(主键)和成绩(次键)组成的组合键
  public static class IntPair implements WritableComparable<IntPair> {

    private int first;
    private int second;

    public IntPair() {
    }

    public IntPair(int first, int second) {
      set(first, second);
    }

    public void set(int first, int second) {
      this.first = first;
      this.second = second;
    }

    public int getFirst() {
      return first;
    }

    public int getSecond() {
      return second;
    }

    @Override
    public void write(DataOutput out) throws IOException {
      out.writeInt(first);
      out.writeInt(second);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
      first = in.readInt();
      second = in.readInt();
    }

    @Override
    public int hashCode() {
      return first * 163 + second;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof IntPair) {
        IntPair ip = (IntPair) o;
        return first == ip.first && second == ip.second;
      }
      return false;
    }

    @Override
    public String toString() {
      return first + "\t" + second;
    }

    @Override
    public int compareTo(IntPair ip) {
      int cmp = compare(first, ip.first);
      if (cmp != 0) {
        return cmp;
      }
      return compare(second, ip.second);
    }

    /**
     * Convenience method for comparing two ints.
     */
    public static int compare(int a, int b) {
      return (a < b ? -1 : (a == b ? 0 : 1));
    }

  }

}
```

&ensp;&ensp;&ensp;&ensp; 我们创建了一个自定义的partitioner以按照组合键的首字段(学科代码)进行分区，即FirstPartitioner，为了按照学科代码和成绩(降序)排序键，我们使用了一个自定义键KeyComparator. 为了按学科代码进行分组，自定义了一个GroupComparator. 只有同时按照学科代码进行分区和分组才能确保同一学科的记录达到同一个Reducer.

运行代码，需要三个参数，第三个参数是reduce个数，这里我们设置为1

hadoop jar mvusndst.jar demo.MaxValueUsingSecondarySort filescore outscore 1


## 路由中的一个实际业务问题

&ensp;&ensp;&ensp;&ensp; 这里的路由可以指广义上的路由，适用于所有物流配送相关问题。这个例子来源于路由系统，以运单77316904537为例，在路由系统实操表它是按下图存储的：

![](06 大数据/06 Hive/路由图片-a.png)
![](06 大数据/06 Hive/路由图片-b.png)

&ensp;&ensp;&ensp;&ensp; 运单在配送过程中经过若干网点（我们把站点、分拣中心/接货仓、仓库、机场，备件库都统称为网点），而在一个网点又会有若干个操作，比如到车、验货、发货、封车等。

&ensp;&ensp;&ensp;&ensp; 该运单经过贵阳亚一分拨中心两次，这属于正常业务场景，我们把这种场景称为A-B-A，还有可能经过三次四次等等。假设现在需要实现下面这个需求：
首先原始数据如下，我们先用Hive做预处理，只提取出三个字段，其中操作顺序是运单实操时间的排列序号。

### 原始输入数据

运单号 网点编码  操作顺序

```
77316904537 4-50  1
77316904537 4-50  2
77316904537 028F019 3
77316904537 028F019 4
77316904537 028F019 5
77316904537 028F003 6
77316904537 028F003 7
77316904537 028F003 8
77316904537 028F003 9
77316904537 851F002 10
77316904537 851F002 11
77316904537 851F002 12
77316904537 851F002 13
77316904537 851F002 14
77316904537 851F002 15
77316904537 851Y006 16
77316904537 851Y006 17
77316904537 851Y006 18
77316904537 851Y006 19
77316904537 851Y006 20
77316904537 851Y006 21
77316904537 851Y006 22
77316904537 851Y006 23
77316904537 851F002 24
77316904537 851F002 25
77316904537 851F002 26
77316904537 851F002 27
77316904537 851F002 28
77316904537 851F002 29
77316904537 851Y009 30
77316904537 851Y009 31
77316904537 851Y009 32
77316904537 851Y009 33
```

### 需要输出数据

要求实现以下输出，增加了一个网点序号。
运单号 网点编码  操作顺序  网点序号

```
77316904537 4-50  1 1
77316904537 4-50  2 1
77316904537 028F019 3 2
77316904537 028F019 4 2
77316904537 028F019 5 2
77316904537 028F003 6 3
77316904537 028F003 7 3
77316904537 028F003 8 3
77316904537 028F003 9 3
77316904537 851F002 10  4
77316904537 851F002 11  4
77316904537 851F002 12  4
77316904537 851F002 13  4
77316904537 851F002 14  4
77316904537 851F002 15  4
77316904537 851Y006 16  5
77316904537 851Y006 17  5
77316904537 851Y006 18  5
77316904537 851Y006 19  5
77316904537 851Y006 20  5
77316904537 851Y006 21  5
77316904537 851Y006 22  5
77316904537 851Y006 23  5
77316904537 851F002 24  6
77316904537 851F002 25  6
77316904537 851F002 26  6
77316904537 851F002 27  6
77316904537 851F002 28  6
77316904537 851F002 29  6
77316904537 851Y009 30  7
77316904537 851Y009 31  7
77316904537 851Y009 32  7
77316904537 851Y009 33  7
```

注意到这个需求是这样的，需要保持原三个字段不变，原始数据的记录数也不变，不去重，在同一个网点连续的操作序号，我们要把它标识为同一个序号，用于标识网点的顺序。有人肯定好奇，这个需求用HiveQL能实现吗？我之前也是尝试过用HiveQL实现，结果都是徒劳的。但是如果要求实现的是下面这样：
运单号 网点编码  操作顺序  网点序号

```
77316904537 4-50  1 1
77316904537 028F019 3 2
77316904537 028F003 6 3
77316904537 851F002 10  4
77316904537 851Y006 16  5
77316904537 851F002 24  6
77316904537 851Y009 30  7
```

也就是将网点去重了，那HiveQL确实是可以实现的，例如使用以下SQL

```
select 
  waybill_code
  ,node_code
  ,rn
  ,row_number() over(distribute by waybill_code sort by rn) node_index
from (
  select
    waybill_code
    ,node_code
    ,rn
    ,lag(node_code, 1) over (distribute by waybill_code sort by rn) as last_node

  from (

    select '77316904537' waybill_code, '4-50'    node_code, 1  rn union all
    select '77316904537' waybill_code, '4-50'    node_code, 2  rn union all
    select '77316904537' waybill_code, '028F019' node_code, 3  rn union all
    select '77316904537' waybill_code, '028F019' node_code, 4  rn union all
    select '77316904537' waybill_code, '028F019' node_code, 5  rn union all
    select '77316904537' waybill_code, '028F003' node_code, 6  rn union all
    select '77316904537' waybill_code, '028F003' node_code, 7  rn union all
    select '77316904537' waybill_code, '028F003' node_code, 8  rn union all
    select '77316904537' waybill_code, '028F003' node_code, 9  rn union all
    select '77316904537' waybill_code, '851F002' node_code, 10 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 11 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 12 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 13 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 14 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 15 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 16 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 17 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 18 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 19 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 20 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 21 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 22 rn union all
    select '77316904537' waybill_code, '851Y006' node_code, 23 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 24 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 25 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 26 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 27 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 28 rn union all
    select '77316904537' waybill_code, '851F002' node_code, 29 rn union all
    select '77316904537' waybill_code, '851Y009' node_code, 30 rn union all
    select '77316904537' waybill_code, '851Y009' node_code, 31 rn union all
    select '77316904537' waybill_code, '851Y009' node_code, 32 rn union all
    select '77316904537' waybill_code, '851Y009' node_code, 33 rn 

  ) tmp
) a

where last_node is null
or node_code <> last_node
;
```

![](06 大数据/06 Hive/hive结果.png)

&ensp;&ensp;&ensp;&ensp; 这个结果是我们要实现的需求的推论。现在假如我们要取第6个网点的到车时间，上面这个结论其实是无法满足的，第6个网点是851F002（贵阳亚一分拨中心），但同时它也是第4个网点，可是原始数据里面是没有网点序号的，似乎可以用网点来关联，但是网点又是重复的，这样我们就没法知道取出来的时间到底是第4个网点的到车时间还是第6个网点的到车时间，所以我们干脆在原始数据里面增加网点的序号，而这需要循环迭代，貌似Hive是无法进行循环迭代的，但是这个问题可以用MapReduce来解决，用Java去实现这个问题。

### Java MapReduce代码

```
package com.jd;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author zhangyuchao1@jd.com
 * <p>
 * 京东路由网点排序算法
 */
public class NodeSortPlus extends Configured implements Tool {

  public static class Map extends Mapper<LongWritable, Text, Text, StrTuple> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String[] lines = value.toString().split("\t");

      context.write(new Text(lines[0]), new StrTuple(lines[0], lines[1], Integer.parseInt(lines[2])));

    }
  }

  public static class Red extends Reducer<Text, StrTuple, StrTuple, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<StrTuple> values, Context context) throws IOException, InterruptedException {
      List<String> al = new ArrayList();
    // 先将StrTuple组合键从迭代器取出并转化为字符串存到List，如果不这么做，会有意想不到的结果
      for (StrTuple iw : values)
        al.add(iw.toString());

      int len = al.size();
      StrTuple[] a = new StrTuple[len];

      int t = 0;
    // 定义一个数组a用于存取自定义的组合键
      for (String st : al) {
        String[] line = st.split("\t");
        a[t++] = new StrTuple(
            line[0],
            line[1],
            Integer.parseInt(line[2])
        );
      }

// 将数组a中的组合键按照排列序号rn从小打到排列
      for (int i = 0; i < len; i++) {
        for (int j = i + 1; j < len; j++) {
          if (a[i].getRn().get() > a[j].getRn().get()) {
            StrTuple tmp = new StrTuple(a[i].getWaybill_code(), a[i].getNode_code(), a[i].getRn());

            a[i].set(a[j].getWaybill_code(),
                a[j].getNode_code(),
                a[j].getRn());
            a[j].set(
                tmp.getWaybill_code(),
                tmp.getNode_code(),
                tmp.getRn());
          }
        }
      }

// 定义数组b，后面排序的时候存放改变之前的数组a，数组a会不断变化
      StrTuple[] b = new StrTuple[len];

// 数组a0保存排好顺序的a，后面不在改变  
      StrTuple[] a0 = new StrTuple[len];

      for (int i = 0; i < len; i++)
        b[i] = new StrTuple(
            a[i].getWaybill_code(),
            a[i].getNode_code(),
            a[i].getRn());

      for (int i = 0; i < len; i++)
        a0[i] = new StrTuple(
            a[i].getWaybill_code(),
            a[i].getNode_code(),
            a[i].getRn());
      
// 77317395096 3-95  1 1
// 77317395096 3-95  2 1
// 77317395096 512F008 3 3
// 77317395096 512F008 4 3
// 77317395096 512F008 5 3
//该步骤将连续的编号置为最小的那个编号，实际上到这一步后，已经可以处理了，如上  
      for (int i = 1; i < len; i++) {
        for (int j = i; j < len; j++)
          if (a[j].getRn().get() == b[j - 1].getRn().get() + 1 && a[j].getNode_code().equals(b[j - 1].getNode_code()))
            a[j].setRn(b[j - 1].getRn());
        for (int k = 0; k < len; k++)
          b[k].set(
              a[k].getWaybill_code(),
              a[k].getNode_code(),
              a[k].getRn());
      }

      StrTuple[] endTuple = new StrTuple[len];
      for (int i = 0; i < len; i++) {
        endTuple[i] = new StrTuple(
            a[i].getWaybill_code(),
            a[i].getNode_code(),
            a[i].getRn());
      }

// 77317395096 3-95  1 1
// 77317395096 3-95  2 1
// 77317395096 512F008 3 2
// 77317395096 512F008 4 2
// 77317395096 512F008 5 2
//该步骤实现最终结果，如上
      for (int i = 1; i < len; i++) {
        for (int j = i; j < len; j++) {
          if (endTuple[j].getRn().get() > b[j - 1].getRn().get())
            for (int k = j; k < len; k++) {
              if (endTuple[k].getRn().get() == b[j].getRn().get() && endTuple[k].getNode_code().equals(b[j].getNode_code())) {
                endTuple[k].setRn(new IntWritable(b[j - 1].getRn().get() + 1));
              }
            }
          break;
        }

        for (int m = 0; m < len; m++)
          b[m] = new StrTuple(
              endTuple[m].getWaybill_code(),
              endTuple[m].getNode_code(),
              endTuple[m].getRn());

      }


      for (int i = 0; i < a.length; i++)
        context.write(a0[i], endTuple[i].getRn());
    }
  }

  @Override
  public int run(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.printf("Usage: %s [generic options] <input> <output> <NumReduceTasks>\\n",
          getClass().getSimpleName());
      ToolRunner.printGenericCommandUsage(System.err);
      return -1;
    }
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "NodeSortPlus");

    job.setInputFormatClass(TextInputFormat.class);

    job.setJarByClass(getClass());

//设置输入文件路径
    FileInputFormat.addInputPath(job, new Path(args[0]));
//设置输出文件路径
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    job.setMapperClass(Map.class);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(StrTuple.class);

//设置reduce个数
    job.setNumReduceTasks(Integer.parseInt(args[2]));
    job.setReducerClass(Red.class);

    job.setOutputKeyClass(StrTuple.class);
    job.setOutputValueClass(IntWritable.class);

    job.setOutputFormatClass(TextOutputFormat.class);

    return job.waitForCompletion(true) ? 0 : 1;

  }

  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new NodeSortPlus(), args);
    System.exit(exitCode);
  }


  /**
   * StrTuple
   * <p>
   * 自定义组合建
   */
  public static class StrTuple implements WritableComparable<StrTuple> {

    private Text waybill_code = new Text();
    private Text node_code = new Text();
    private IntWritable rn = new IntWritable();

    public Text getWaybill_code() {
      return waybill_code;
    }

    public void setWaybill_code(Text waybill_code) {
      this.waybill_code = waybill_code;
    }

    public Text getNode_code() {
      return node_code;
    }

    public void setNode_code(Text node_code) {
      this.node_code = node_code;
    }

    public IntWritable getRn() {
      return rn;
    }

    public void setRn(IntWritable rn) {
      this.rn = rn;
    }


    public StrTuple() {
      set(new Text(), new Text(), new IntWritable()
      );
    }


    public void set(Text waybill_code,
            Text node_code,
            IntWritable rn) {
      this.waybill_code = waybill_code;
      this.node_code = node_code;
      this.rn = rn;

    }

    public StrTuple(String waybill_code,
            String node_code,
            int rn) {
      set(new Text(waybill_code),
          new Text(node_code),
          new IntWritable(rn));
    }

    public StrTuple(Text waybill_code,
            Text node_code,
            IntWritable rn) {
      set(waybill_code, node_code, rn);
    }


    @Override
    public void write(DataOutput out) throws IOException {
      waybill_code.write(out);
      node_code.write(out);
      rn.write(out);

    }

    @Override
    public void readFields(DataInput in) throws IOException {

      waybill_code.readFields(in);
      node_code.readFields(in);
      rn.readFields(in);
    }

    @Override
    public int hashCode() {
      return waybill_code.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof StrTuple) {
        StrTuple ip = (StrTuple) o;
        return waybill_code.equals(ip.waybill_code) && node_code.equals(ip.node_code) && rn.equals(ip.rn);
      }
      return false;
    }

    @Override
    public String toString() {
      return waybill_code + "\t" + node_code + "\t" + rn;

    }

    /**
     * Convenience method for comparing two ints.intin
     */
    public static int compare(int a, int b) {
      return (a < b ? -1 : (a == b ? 0 : 1));
    }


    @Override
    public int compareTo(StrTuple tp) {
      int cmp = waybill_code.compareTo(tp.waybill_code);
      if (cmp != 0) {
        return cmp;
      }
      return compare(rn.get(), tp.rn.get());
    }

  }

}
```


&ensp;&ensp;&ensp;&ensp; 我们自定义了一个组合键StrTuple，用于存放运单号、网点编码、排列序号，实际的Hive表中有很多字段，我们也可以自定义一个包含所有字段的组合键，但那样会产生许多冗余信息，排序的时候只需要这三个字段就足够了，另外这也可以提高它的通用性，因为任何配送相关的问题，我们都可以预处理成这三个字段，其它事情交给Hive处理。

map: <Text, StrTuple>，map函数很简单，只需要直接输出运单号和以及我们自定义的组合建StrTuple (运单号、网点编码、排列序号组成的)。接下来同一个运单号的记录都会被送到同一个reducer函数，我们把所有排序操作都放到reducer函数处理，这些都是在内存中完成，但幸好一个运单也就是只有十几天记录，所以程序是完全没问题的。

### 在京东IDE平台创建任务自动执行

将代码编译打包后（例如叫ndusndst.jar）与下面的shell脚本归档到一个zip文件，以便放到大数据平台自动调用

```
#!/bin/bash
# 需要两个参数
#   $1 reduce个数
#   $2 输入文件路径
#   $3 输出文件路径
##########################################################################

basepath=$(cd $(dirname $0); pwd)

num_reduce_tasks=$1
input_path=$2
tb=$3

my_dir="/user/mart_coo/zhangyuchao1"
db=app
#tb_tmp=${0%.*}

if [[ -z $1 ]]
then
    num_reduce_tasks=45
fi

if [[ -z $2 ]]
then
    input_path="/user/mart_coo/app.db/app_realtime_waybill_sort_time"
fi

if [[ -z $3 ]]
then
#    tb=$(echo ${tb_tmp} | gawk -F "/" '{print $NF}')
    tb=$(basename $0 .sh)
fi

echo `date +%F\ %T` ===== start =====

hadoop fs -rm -r ${my_dir}/${tb}

hadoop jar ${basepath}/ndusndst.jar com.jd.NodeUsingSecondarySort "${input_path}" "${my_dir}/${tb}" ${num_reduce_tasks}

num=$(hadoop fs -ls ${my_dir}/${tb}/_SUCCESS | wc -l)

if [[ $num -eq 1 ]]
then
    echo `date +%F\ %T` = $num ==== 成功 ======
else
    echo `date +%F\ %T` = $num ==== 失败 ======
    exit 1
fi

sql="
use ${db};
create external table if not exists ${tb} (
   waybill_code         string
  ,node_code            string
  ,rn                   int
  ,min_rn               int
) row format
  delimited fields terminated by '\t'
location
  '${my_dir}/${tb}';
"

hive -e "${sql}"

echo `date +%F\ %T` ===== end ======
```

我们使用4.0的任务，便于调整执行参数，任务名是

exe_mapred_realtime_waybill_node_sort

需要三个参数

45;/user/mart_coo/app.db/app_realtime_waybill_sort_time;mapred_realtime_waybill_node_sort

&ensp;&ensp;&ensp;&ensp; 参数之间是按照“；”分号分割的，第一个参数是reduce个数，这里我们设置为45（默认是1个，多了或者少了均不是好事，如果默认1个，可能会执行一两个小时），第二个是输入文件路径(通常是某个Hive表的表路径)，第三个参数是MapReduce保存的结果表名。

## 使用辅助排序实现的网点排序

### 辅助排序实现的网点排序Java MapReduce代码

```
package com.jd;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author zhangyuchao1@jd.com
 * <p>
 * 路由网点排序算法
 */
public class NodeUsingSecondarySort extends Configured implements Tool {

  /**
   * 自定义分区
   * 按第一个字段即运单号分区
   */
  public static class FirstPartitioner
      extends Partitioner<StrTuple, NullWritable> {

    @Override
    public int getPartition(StrTuple key, NullWritable value, int numPartitions) {
      return Math.abs(key.waybill_code.hashCode() & 2147483647) % numPartitions;
    }
  }

  /**
   * 自定义排序
   */
  public static class KeyComparator extends WritableComparator {
    protected KeyComparator() {
      super(StrTuple.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
      StrTuple ip1 = (StrTuple) w1;
      StrTuple ip2 = (StrTuple) w2;
      return ip1.compareTo(ip2);
    }
  }

  /**
   * 自定义分组
   * 按第一个字段即运单号分组
   */
  public static class GroupComparator extends WritableComparator {
    protected GroupComparator() {
      super(StrTuple.class, true);
    }

    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
      StrTuple ip1 = (StrTuple) w1;
      StrTuple ip2 = (StrTuple) w2;
      return ip1.getWaybill_code().compareTo(ip2.getWaybill_code());
    }
  }


  public static class Map extends Mapper<LongWritable, Text, StrTuple, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String[] lines = value.toString().split("\t");

      context.write(new StrTuple(lines[0],
              lines[1],
              Integer.parseInt(lines[2])),
          NullWritable.get());
    }
  }

  public static class Red extends Reducer<StrTuple, NullWritable, StrTuple, IntWritable> {
    @Override
    protected void reduce(StrTuple key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
      List<String> al = new ArrayList();

      for (NullWritable iw : values)
        al.add(key.toString());

      int len = al.size();
      StrTuple[] a = new StrTuple[len];

      int t = 0;
      for (String st : al) {
        String[] line = st.split("\t");
        a[t++] = new StrTuple(
            line[0],
            line[1],
            Integer.parseInt(line[2])
        );
      }

    // 记录在到达reducer后，已经按照rn即操作顺序从小到大排列，因此下面的代码不需要了
//      for (int i = 0; i < len; i++) {
//        for (int j = i + 1; j < len; j++) {
//          if (a[i].getRn().get() > a[j].getRn().get()) {
//            StrTuple tmp = new StrTuple(a[i].getWaybill_code(), a[i].getNode_code(), a[i].getRn());
//
//            a[i].set(a[j].getWaybill_code(),
//                a[j].getNode_code(),
//                a[j].getRn());
//
//            a[j].set(
//                tmp.getWaybill_code(),
//                tmp.getNode_code(),
//                tmp.getRn());
//
//          }
//        }
//      }

      StrTuple[] b = new StrTuple[len];
      StrTuple[] a0 = new StrTuple[len];

      for (int i = 0; i < len; i++)
        b[i] = new StrTuple(
            a[i].getWaybill_code(),
            a[i].getNode_code(),
            a[i].getRn());

      for (int i = 0; i < len; i++)
        a0[i] = new StrTuple(
            a[i].getWaybill_code(),
            a[i].getNode_code(),
            a[i].getRn());

      for (int i = 1; i < len; i++) {
        for (int j = i; j < len; j++)
          if (a[j].getRn().get() == b[j - 1].getRn().get() + 1 && a[j].getNode_code().equals(b[j - 1].getNode_code()))
            a[j].setRn(b[j - 1].getRn());
        for (int k = 0; k < len; k++)
          b[k].set(
              a[k].getWaybill_code(),
              a[k].getNode_code(),
              a[k].getRn());
      }

      StrTuple[] endTuple = new StrTuple[len];
      for (int i = 0; i < len; i++) {
        endTuple[i] = new StrTuple(
            a[i].getWaybill_code(),
            a[i].getNode_code(),
            a[i].getRn());
      }


      for (int i = 1; i < len; i++) {
        for (int j = i; j < len; j++) {
          if (endTuple[j].getRn().get() > b[j - 1].getRn().get())
            for (int k = j; k < len; k++) {
              if (endTuple[k].getRn().get() == b[j].getRn().get() && endTuple[k].getNode_code().equals(b[j].getNode_code())) {
                endTuple[k].setRn(new IntWritable(b[j - 1].getRn().get() + 1));
              }
            }
          break;
        }

        for (int m = 0; m < len; m++)
          b[m] = new StrTuple(
              endTuple[m].getWaybill_code(),
              endTuple[m].getNode_code(),
              endTuple[m].getRn());

      }

      for (int i = 0; i < a.length; i++)
        context.write(a0[i], endTuple[i].getRn());
    }
  }

  @Override
  public int run(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.printf("Usage: %s [generic options] <input> <output> <NumReduceTasks>\\n",
          getClass().getSimpleName());
      ToolRunner.printGenericCommandUsage(System.err);
      return -1;
    }
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "NodeSort");

    job.setInputFormatClass(TextInputFormat.class);

    job.setJarByClass(getClass());
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    job.setMapperClass(Map.class);

    job.setMapOutputKeyClass(StrTuple.class);
    job.setMapOutputValueClass(NullWritable.class);


    job.setNumReduceTasks(Integer.parseInt(args[2]));
    job.setReducerClass(Red.class);

    job.setPartitionerClass(FirstPartitioner.class);
    job.setSortComparatorClass(KeyComparator.class);
    job.setGroupingComparatorClass(GroupComparator.class);

    job.setOutputKeyClass(StrTuple.class);
    job.setOutputValueClass(IntWritable.class);

    job.setOutputFormatClass(TextOutputFormat.class);

    return job.waitForCompletion(true) ? 0 : 1;

  }

  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new NodeUsingSecondarySort(), args);
    System.exit(exitCode);
  }

  /**
   * StrTuple
   * <p>
   * 自定义组合建
   */
  public static class StrTuple implements WritableComparable<StrTuple> {

    private Text waybill_code = new Text();
    private Text node_code = new Text();
    private IntWritable rn = new IntWritable();

    public Text getWaybill_code() {
      return waybill_code;
    }

    public void setWaybill_code(Text waybill_code) {
      this.waybill_code = waybill_code;
    }

    public Text getNode_code() {
      return node_code;
    }

    public void setNode_code(Text node_code) {
      this.node_code = node_code;
    }

    public IntWritable getRn() {
      return rn;
    }

    public void setRn(IntWritable rn) {
      this.rn = rn;
    }

    public StrTuple() {
      set(new Text(), new Text(), new IntWritable()
      );
    }

    public void set(Text waybill_code,
            Text node_code,
            IntWritable rn) {
      this.waybill_code = waybill_code;
      this.node_code = node_code;
      this.rn = rn;

    }

    public StrTuple(String waybill_code,
            String node_code,
            int rn) {
      set(new Text(waybill_code),
          new Text(node_code),
          new IntWritable(rn));
    }

    public StrTuple(Text waybill_code,
            Text node_code,
            IntWritable rn) {
      set(waybill_code, node_code, rn);
    }

    @Override
    public void write(DataOutput out) throws IOException {
      waybill_code.write(out);
      node_code.write(out);
      rn.write(out);

    }

    @Override
    public void readFields(DataInput in) throws IOException {

      waybill_code.readFields(in);
      node_code.readFields(in);
      rn.readFields(in);
    }

    @Override
    public int hashCode() {
      return waybill_code.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof StrTuple) {
        StrTuple ip = (StrTuple) o;
        return waybill_code.equals(ip.waybill_code) && node_code.equals(ip.node_code) && rn.equals(ip.rn);
      }
      return false;
    }

    @Override
    public String toString() {
      return waybill_code + "\t" +
          node_code + "\t" +
          rn;
    }

    /**
     * Convenience method for comparing two ints.intin
     */
    public static int compare(int a, int b) {
      return (a < b ? -1 : (a == b ? 0 : 1));
    }

    @Override
    public int compareTo(StrTuple tp) {
      int cmp = waybill_code.compareTo(tp.waybill_code);
      if (cmp != 0) {
        return cmp;
      }
      return compare(rn.get(), tp.rn.get());
    }

  }

}
```

### 说明

&ensp;&ensp;&ensp;&ensp; 与上一个例子一样，我们同样自定义了一个组合键StrTuple，用于存放运单号、网点编码、排列序号。不同点是我们将组合键StrTuple作为map函数和reduce函数的输出键，而非运单号，便于使用辅助排序(或者叫二次排序)。为此，我们自定义了一个分区FirstPartitioner，只按照组合键的第一个字段运单号分区，一个排序KeyComparator，先按照组合键的第一个字段运单号排序，再按操作顺序从小到大排序，一个分组GroupComparator，只按照组合键的第一个字段运单号分组。这样，同一个运单的记录都会到达同一个reducer，并且已经排好序。另外，map的输出值上不需要附加任何信息，因此我们用NullWritable。

&ensp;&ensp;&ensp;&ensp; 对比两个实现，在reducer中排序是在内存里进行的，值得庆幸的是一个运单的记录数并不多。使用辅助排序的代码在reducer中少了一次排序，它的排序是利用MapReduce框架完成（我们尽可能这么做），实际中应该用哪个取决于它们的执行速度。
