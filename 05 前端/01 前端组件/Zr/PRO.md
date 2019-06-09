## PRO的使用方法

PRO作为一款集众多组件于一身，并且支持响应式加载同时遵循Zr加载策略。那么PRO的使用场景和社会价值也在不同方面能体现

首先

PRO分为两个版本：Flex版本和浮动布局版本(Float)，为何做了2种，那是为了简化代码并且提供更好的效果来敲定的

>Flex的版本使用方式

    
    $ git clone http://git.jd.com/PRO/flex.git --depth=1
    $ cd zr-pro-flex
    $ npm install
    $ gulp DEV # 启动后，请选择index.html。如果未启动，请打开浏览器访问 http://localhost:9696/index.html


>Float版本的使用方式

    //Float版本的使用方式
     目前正在测试阶段，敬请期待......
     
以上两种都是用的前端服务去启动的，其他方式启动呢？
>Apache、Nginx、Forever、pm2等

    <!--@注意还是从git上clone，目前只能从git.jd.com进行下载，其他用户暂时无法使用-->
    步骤一：$ git clone http://git.jd.com/PRO/flex.git --depth=1
    步骤二：切换到对应的目录，然后将目录移动到对应的服务（Apache、Nginx、Forever、pm2等）指定的启动位置
    步骤三：启动服务，然后就可以直接预览了<br>
    当然过程中有任何问题可以及时联系：咚咚群：7141716。或者发送邮件到:zr_team@jd.com<br>
    我们会全力支持~~
    
启动后就能看到工程页面，同时能快速的介入开发使用上了。如下是展示截图

![可视化截图一](http://storage.360buyimg.com/gitdemo/first.png "可视化截图一")

可视化截图一

![可视化截图二](http://storage.360buyimg.com/gitdemo/second.png "可视化截图二")

可视化截图二

![列表页](http://storage.360buyimg.com/gitdemo/three.png "列表页")

列表页

![详情页](http://storage.360buyimg.com/gitdemo/four.png "详情页")

详情页


    
