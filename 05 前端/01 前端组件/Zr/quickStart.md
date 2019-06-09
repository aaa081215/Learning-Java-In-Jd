## 快速入门方法


想要尽快掌握Zr的使用方法，请观看[视频](http://v.jd.com/linfo/toPlay/5589) 内容

如果视频中涉及的展示代码，请访问[京东Git](http://git.jd.com/Zr/demo)

好了，直接上知识点

>第一步

    //环境配置
    //引入css样式
    <link type="text/css" rel="stylesheet" href="//storage.360buyimg.com/1.3.3/zr/css/cdn_zr.min.css" />
    //引入js文件
    <script type="text/javascript" language="javascript" src="//storage.360buyimg.com/1.3.3/zr.min.js"></script>
    //初始化全局配置
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
     
>第二步

    //掌握以下个方法
    //准备方法
    Zr.ready(function(){
        //Zr 各个模块已经准备就绪，可以加载使用了
    })
    //配置方法
    Zr.config(options)
    //添加自定义组件方法
    Zr.add(name/path,callBack,config)
    //使用内置或者自定义组件的方法
    Zr.use(name/path,[......],callBack)
    
以上几个方法具体的使用规则请查看[快速上手](http://zr.jd.com/docs?languageCode=CN&columnUid=40ad19be535e477596a033f9fea2bf7d&directoryUid=aed4199658924584a1b674ad05c3e2ec&directoryName=%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B)

当然，配置完第一步后，就直接可以使用对应的组件了。第二步是为了更好的掌握Zr的使用技巧。记得多看视频教程更加快速入门！

> 大招

如果针对组件如何组合成一个系统，并且能支持多端显示等问题，目前可以直接[clone项目](http://git.jd.com/PRO/flex)到本地运行,[点击查看预览效果](http://zr.jd.com/static/tmpl/dashboard/workboard.html)

       
