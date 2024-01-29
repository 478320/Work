# WarmUp

该项目使用了Springboot技术开发Spring程序，使用了mybatisplus框架，mysql，redis数据库，以及hutool工具类等技术

该项目包含了配置类（包含跨域请求处理过滤器，雪花算法传递问题处理器），表现层包（包含数据校验拦截器），dto（传输数据包），实体类包，自定义异常包，数据层包，业务层包，以及utils工具包（各种常量，以及拦截器，和ThreadLocal工具类）

该项目实现了简单的注册，登录，登录校验，新增代表，查询代表，缓存代表，更改代表内容，更改代表状态，和删除代表等功能

对于异常的处理，以及数据的传输等方面待改进

通过dockerfile部署项目，对应的mysql与redis后可启动，暂未编写dockerCompose