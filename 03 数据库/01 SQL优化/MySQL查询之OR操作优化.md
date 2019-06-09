## OR查询优化
在日常MySQL语句优化中，经常遇到使用OR关键字而导致查询出现性能问题的情况，对于简单OR查询，可以将查询按照OR两边条件涉及表和字段分为三种：

	1. OR两边条件涉及表和字段相同，如TB1.C1=10001 OR TB1.C1=10002
	2. OR两边条件涉及表相同但字段不同，如TB1.C1=10001 OR TB1.C2='ABC' 
	3. OR两边条件涉及表和字段不同，如TB1.C1=10001 OR TB2.C2='ABC'



## OR两边条件涉及表和字段相同
针对``` TB1.C1=10001 OR TB1.C1=10002 ```场景，可以将OR操作修改为IN子句，如``` TB1.C1 IN (10001,1002) ```


## OR两边条件涉及表相同但字段不同
针对``` TB1.C1=10001 OR TB1.C2='ABC' ```场景，可以将OR操作修改为UNION子句，如对于原始SQL：

	SELECT * 
	FROM TB1
	WHERE TB1.C1=10001
	OR TB1.C2='ABC'

由于在MySQL 5.5版本及其之前版本不允许在一个查询中使用同一张表的多个索引，虽然在MySQL 5.6和MySQL 5.7中提供INDEX MERGE特性，但由于该特性存在很多BUG，通常会在生产环境禁用INDEX MERGE特性，因此即使在表TB1的C1和C2列上都存在索引，该查询也可能会使用全部扫描。

可以将SQL转换为：

	SELECT * 
	FROM TB1
	WHERE TB1.C1=10001
	UNION ALL
	SELECT * 
	FROM TB1
	WHERE TB1.C2='ABC'
	AND TB1.C1<>1001

修改SQL后，建议对TB1创建索引```IDX_C1(C1)``` 和``` IDX_C2_C1(C2,C1) ```,UNION ALL关联的两个子查询可以分别通过这两索引来提升查询效率。

对于条件 ``` TB1.C2='ABC' AND TB1.C1<>1001 ```,当表中存在索引``` IDX_C2_C1(C2,C1) ```时：

1. 对于MySQL 5.5及其之前版本，可以通过索引按照```TB1.C2='ABC'``来进行数据过滤。
2. 对于MySQL 5.6及其后续版本，由于引入ICP(Index Condition Pushdown)特性，可以通过索引按照```TB1.C2='ABC' AND T1.c1<>1001```来进行数据过滤, 减少MySQL存储引擎层到MySQL Server层的数据传输，提升查询效率。


## OR两边条件涉及表和字段不同
对于``` TB1.C1=10001 OR TB2.C2='ABC' ```场景，由于两边涉及表不同，因此可以分别使用两表的索引来进行数据过滤。

## 注意事项
在开发过程中，所有查询优化都应该以最终测试为准。
