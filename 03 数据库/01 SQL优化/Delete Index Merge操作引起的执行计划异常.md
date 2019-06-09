##Index Merge介绍

在MySQL 5.6版本中引入Index Merge特性，当查询SQL中某个表包含多个过滤条件(过滤条件使用AND或OR进行连接)，可以通过该表的多个索引来进行条件过滤，再将过滤后的结果集进行合并(INTERSECT或UNION)。

例如表TB1上C1和C2上分别建立索引，对于SQL语句：
	
	SELECT *
	FROM TB1
	WHERE TB1.C1=100011
	OR TB1.C2='ABC'

 可以分别先按照 ``` SELECT * FROM TB1 WHERE TB1.C1=100011 ``` 和 ``` SELECT * FROM TB1 WHERE TB1.C2='ABC' ``` 得到两个结果集，再把两个结果集进行UNINON操作，得到最终执行结果。

在MySQL 5.6和MySQL 5.7版本中，默认启用INDEX MERGE特性。


##Index Merge异常

生产环境使用MySQL 5.7.19版本，查询SQL为：

    SELECT 
	P.P_ID,
	P.BATCH_NO,
	P.WAVE_NO,
	P.WAVE_TYPE
	FROM
	OB_CHECK_P P 
	INNER JOIN OB_CHECK_D D 
	ON P.GOODS_NO = D.GOODS_NO 
	AND P.BATCH_NO = D.BATCH_NO 
	AND P.OUTBOUND_NO = D.OUTBOUND_NO 
	AND P.OUTBOUND_TYPE = D.OUTBOUND_TYPE 
	AND P.ORG_NO = D.ORG_NO 
	AND P.DISTRIBUTE_NO = D.DISTRIBUTE_NO 
	AND P.WAREHOUSE_NO = D.WAREHOUSE_NO 
	WHERE P.YN = 0 
	AND P.BATCH_NO= 'OBxxxxxxxxxxxxxxxxxxxx'
	AND P.OUTBOUND_NO = 'CSLxxxxxxxxxxxxxxxxxxxx' 
	AND P.OUTBOUND_TYPE = 10 
	AND P.ORG_NO = '650'
	AND P.DISTRIBUTE_NO = '605'
	AND P.WAREHOUSE_NO = '213';

上面INNER JOIN关联的两个表OB_CHECK_P 和OB_CHECK_D 都索引存在索引```idx_outboundno(OUTBOUND_NO)```和索引```idx_batchno(BATCH_NO)```，且两个索引选择性极高(单表数据200万，满足索引条件数据12条)，因此上面查询使用任意一个索引都可以快速返回数据，但MySQL在生成查询优化器时认为同时使用两个索引能获得更好查询性能，生成执行计划如下：

    +----+-------------+-------+------------+-------------+----------------------------------------+----------------------------+---------+------+---------+----------+----------------------------------------------------------+
	| id | select_type | table | partitions | type        | possible_keys                          | key                        | key_len | ref  | rows    | filtered | Extra                                                    |
	+----+-------------+-------+------------+-------------+----------------------------------------+----------------------------+---------+------+---------+----------+----------------------------------------------------------+
	|  1 | SIMPLE      | D     | NULL       | index_merge | idx_outboundno,idx_batchno             | idx_outboundno,idx_batchno | 153,93  | NULL |       1 |    10.00 | Using intersect(idx_outboundno,idx_batchno); Using where |
	|  1 | SIMPLE      | P     | NULL       | ALL         | idx_outboundno,idx_goodsno,idx_batchno | NULL                       | NULL    | NULL | 1995517 |     0.00 | Using where; Using join buffer (Block Nested Loop)       |
	+----+-------------+-------+------------+-------------+----------------------------------------+----------------------------+---------+------+---------+----------+----------------------------------------------------------+

在生成执行计划的过程中，MYSQL查询优化器会按照一定规则对SQL进行等价转换，原始SQL中是对表```OB_CHECK_P```进行条件过滤，但由于INNER JOIN的条件中包含```P.OUTBOUND_NO = D.OUTBOUND_NO```和```P.BATCH_NO = D.BATCH_NO```， 因此可以推导出``` D.OUTBOUND_NO=P.OUTBOUND_NO='CSLXXXXXXXXXXXXXXXXXXXX' ```, 因此查询计划中出现对表 ```OB_CHECK_D``` 进行索引查找且使用两个索引进行INTERSECT操作。在本例中INTERSECT操作并不会影响性能，但会影响整条SQL其他部分的执行，使得需要对表OB_CHECK_P执行全部扫描，最终导致SQL执行出现性能问题。

##改写SQL屏蔽INDEX MERGE

由于之前遇到过INDEX MERGE操作导致性能问题的案例，我们优先考虑如何避免SQL使用INDEX MERGER,一个办法就是修改SQL让其只能使用其中一个索引，因此尝试将SQL修改为：

    SELECT 
	P.P_ID,
	P.BATCH_NO,
	P.WAVE_NO,
	P.WAVE_TYPE
	FROM
	OB_CHECK_P P 
	INNER JOIN OB_CHECK_D D 
	ON P.GOODS_NO = D.GOODS_NO 
	AND P.BATCH_NO = D.BATCH_NO 
	AND P.OUTBOUND_NO = D.OUTBOUND_NO 
	AND P.OUTBOUND_TYPE = D.OUTBOUND_TYPE 
	AND P.ORG_NO = D.ORG_NO 
	AND P.DISTRIBUTE_NO = D.DISTRIBUTE_NO 
	AND P.WAREHOUSE_NO = D.WAREHOUSE_NO 
	WHERE P.YN = 0 
	AND P.BATCH_NO= 'OBxxxxxxxxxxxxxxxxxxxxxxx'
	AND CONCAT(P.OUTBOUND_NO,’’) = 'CSLxxxxxxxxxxxxxxxxxx' 
	AND P.OUTBOUND_TYPE = 10 
	AND P.ORG_NO = '650'
	AND P.DISTRIBUTE_NO = '605'
	AND P.WAREHOUSE_NO = '213';

通过对索引列增加函数计算操作来使SQL无法使用```idx_outboundno```索引，只能使用idx_batchno进行索引查找，发现SQL执行恢复正常。

## 禁用INDEX MERGE特性

为快速解决问题，不可能要求研发同事修改SQL并重新上线，因此选择修改MySQL参数optimizer_switch来禁用INDEX MERGE，而当前很多生产服务器都已禁用INDEX MERGE且运行正常。
在禁用INDEX MERGE选项后，修改操作只对新连接生效，于是将应用服务重启以快速恢复，新的SQL执行计划为：

    +----+-------------+-------+------------+------+----------------------------------------+----------------+---------+-------+------+----------+------------------------------------+
	| id | select_type | table | partitions | type | possible_keys                          | key            | key_len | ref   | rows | filtered | Extra                              |
	+----+-------------+-------+------------+------+----------------------------------------+----------------+---------+-------+------+----------+------------------------------------+
	|  1 | SIMPLE      | D     | NULL       | ref  | idx_outboundno,idx_batchno            | idx_outboundno | 153     | const |    7 |     0.71 | Using where                        |
	|  1 | SIMPLE      | P     | NULL       | ref  | idx_outboundno,idx_goodsno,idx_batchno | idx_outboundno | 153     | const |   12 |     0.42 | Using index condition; Using where |
	+----+-------------+-------+------------+------+----------------------------------------+----------------+---------+-------+------+----------+------------------------------------+

## 额外优化建议
在数据库使用过程中，对多表的记录关联查询时关联条件应该使用”标识度”较高的属性列，在上面的SQL中，INNER JOIN使用7个列来进行关联，首先关联效率较低，影响查询性能，其次如果这7列属性不能”唯一确定一条记录”，则关联结果存在数据重复或关联数据不正确。

举个栗子,”学生课程表”包含下面信息：

	student_course_id 自增主键
	student_id 学生编号
	course_id 课程编号

“学生成绩表”包含下面信息

	student_course_id 主键(外键关联学生课程表)
	student_id 学生编号
	course_id 课程编号
	scrore 课程成绩

假设为了特殊的功能需求，我们把学生的班级\姓名\年龄\体重等信息冗余到学生课程表和学生成绩表，在查询学生成绩时需要关联”学生课程表”和”学生成绩表”，此时不能使用学生班级\姓名\年龄\体重等字段来进行关联，这样很难保证关联到的查询结果集”准确”，如果学生学号+课程编号```(student_id+course_id)```可以唯一确定一条学生课程成绩则可以使用学生学号+课程编号来进行关联，但如果存在某位学生重修某门课程，按照学生学号+课程编号进行关联查询该学生此门课程成绩就会得到4条记录，其中两条记录属于”错误关联记录”，这种场景下，只能使用student_course_id来进行关联才能获得正确查询结果。

在对数据库进行反范式化操作来提升查询性能时，应该重点关注冗余字段的使用，尽量避免在JOIN条件和WHERE条件中使用容易字段，以避免出现异常。
