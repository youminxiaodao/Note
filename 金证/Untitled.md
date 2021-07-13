1.准备开发遗留文档，待追踪



# 前端

2.验证kui.core.src.js中reqMsgHeaderKoca OP_SITE是否可以取到值

3.Koca固定入参需与kbss做对比测试，入参需要与kbss完全一致

4.koca ajax请求需带上charset=utf-8

5.ajax-pushlet涉及到文件导入导出/清算case，之后需要修改为koca版本（待后续koca版本修改）





# Apache配置

6.war包修改存放路径，尽量放在kweb路径下，不放在webapps

7.tomcat中修改的部分，部署文档中，需高亮标注，标明修改原因

8.文档中需要标明修改前置条件

xxxx host中的appBase原先时webapps，现已移除，但需要更新进部署文档中（server.xml）



9.apache负载均衡配置中，需要填写上配置文件https.conf

10.tomcat中的server.xml新增的service需要指定docBase







配置文件需要加密

多数据源配置

移除无用filter



测试：接口延迟/响应时间             --------50ms