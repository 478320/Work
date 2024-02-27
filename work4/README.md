# WarmUp

该项目使用了Springboot技术开发Spring程序，使用了mybatisplus框架，mysql，redis数据库，以及hutool工具类，jastpt加密，阿里云OOS，SpringSecurity安全框架等技术

该项目包含了配置类（包含跨域请求处理过滤器，雪花算法传递问题处理器，SpringSecurity配置类），表现层包（包含全局异常处理器），dto（传输数据包），实体类包，自定义异常包，过滤器包（用户登录token处理过滤器），数据层包，业务层包（包含UserDetail的具体实现），以及utils工具包（各种常量，以及加解密工具类，oos对象上传工具类，更改返回请求信息到前端的web工具类，雪花算法传递json处理工具类）

该项目实现了简单的注册，登录，登录校验，更改用户信息，发布文章，查询文章，点赞文章，文章热榜，文章新榜，等等功能

对于查询文章等功能的redis缓存暂未添加实现，对于二级以上等多级评论功能尚未了解

通过dockerfile部署项目，部署对应的mysql与redis后可启动