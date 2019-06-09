## 如何配置开发环境
这章讲述的是使用Zr的时候如何配置开发环境

具体使用的文档地址：[http://zr.jd.com](http://zr.jd.com)

>配置方式一
（前端配置方案）

第一步：涉及到前端配置的都需要准备安装好[Node.js](https://nodejs.org/en/)

第二步：按照如下方式引入即可

    <!--css样式请放在head标签内部-->
    <link type="text/css" rel="stylesheet" href="//storage.360buyimg.com/1.3.3/zr/css/cdn_zr.min.css" />
    <!--以下script请放到</body>前-->
    <script type="text/javascript" language="javascript" src="//storage.360buyimg.com/1.3.3/zr.min.js"></script>
    <script type="text/javascript">
    //配置方法；
    Zr.config({
        //配置plugin_modules的目录；
        baseUrl:"/js/plugin_modules/",
        //开发模式，默认关闭；
        requestTime:true
    })
    //初始入口，Zr各个模块准备就绪后调用；
    Zr.ready(function(){
        //使用相应的模块；
        Zr.use("jquery","./js/code",function(zr,$,code){
            //自动注入了jquery组件；
            //自动注入了./js/code组件；
        })
    })
    </script>
    
初始化配置完毕，大家可以直接在项目中使用了，具体如何使用和配置请观看[视频集合](http://zr.jd.com/docs?languageCode=CN&columnUid=40ad19be535e477596a033f9fea2bf7d&directoryUid=a19bb2a947224ea6a8587987ac183280&directoryName=Javascript%E7%89%88%E6%9C%AC)

第三步：直接启动基于http-server的服务，比如直接使用http-server（没有安装就按照提示安装即可）。或者使用pm2、forever，如果提示没安装请按照提示安装即可。


>配置方式二
（服务端配置方式：Tomcat，Nginx等）

其实这个更加简单了，直接将配置好的页面放置到对应的服务指定根目录即可，然后启动应用，直接访问就能看到效果了。如果有更多问题，请参考[Flex版本的PRO工程](http://zr.jd.com/docs?languageCode=CN&columnUid=40ad19be535e477596a033f9fea2bf7d&directoryUid=a19bb2a947224ea6a8587987ac183280&directoryName=Javascript%E7%89%88%E6%9C%AC)


