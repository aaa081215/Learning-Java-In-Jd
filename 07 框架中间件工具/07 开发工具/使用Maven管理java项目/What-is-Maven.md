# Maven是什么?

Maven是一个项目管理工具，它包含了一个项 目对象模型 (Project Object Model)，一组标准集合，一个项目生命周期(Project Lifecycle)，一个依赖管理系统(Dependency Management System)，和用来运行定义在 生命周期阶段(phase)中插件(plugin)目标(goal)的逻辑。 当你使用Maven的时候，你用一个明确定义的项目对象模型来描述你的项目，然后 Maven 可以应用横切的逻辑， 这些逻辑来自一组共享的（或者自定义的）插件.

### 1.1 Maven的哲学

1. 约定胜于配置

2. 统一的构建接口

   每当你看到一个使用 Maven 的项目如 Apache Wicket7 ，你就可以假设你能签出它的源码然后使用 mvn install 构建它，没什么好争论的。 你知道点火开关在哪里，你知道油门在右边，刹车在左边。    

3. IDE无关

4. Maven项目

   1. POM文件
      1. Maven的核心思想都体现在POM文件中了，POM是Project Object Model的缩写，项目对象模型。一个项目的代码、测试代码（比如JUnit测试代码）、资源（比如模板文件和配置文件）、依赖的包等，都是一个项目构建中的关键信息。POM文件就是一个描述这些信息的XML文件，位于项目的根目录下。 
   2. 坐标
   3. POM组织
      1. ![super-pom](D:/Work/Prjs/Docs/JavaCourse/07%20%E6%A1%86%E6%9E%B6%E4%B8%AD%E9%97%B4%E4%BB%B6%E5%B7%A5%E5%85%B7/07%20%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7/%E4%BD%BF%E7%94%A8Maven%E7%AE%A1%E7%90%86java%E9%A1%B9%E7%9B%AE/img/super-pom.png) 
   4. 依赖管理
   5. 仓库
      1. 获取依赖
      2. 发布

5. 构建的生命周期、阶段和目标

   1. 通过插件管理构建过程

      ![maven-overview](img\maven-overview.png) 