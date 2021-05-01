第十三章：三、config客户端配置与测试
1.config客户端：是什么？
springcloud config为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供一个中心化的
外部配置。
2.怎么玩？
springcloud config分为服务端和客户端两部分。
服务端也称为分布式配置中心，他是一个独立的微服务应用，用来连接配置服务器并且为客户端提供获取配置信息，加密/解密信息等访问接口。
客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。
配置服务器默认采用git来存储配置信息，这样有助于对环境配置进行版本管理，并且可以通过git客户端工具来方便地管理和访问配置内容。

3.搭建步骤：
第一步：新建module：springcloud_config_client_3355

第二步：pom

第三步：bootstrap.yml
application.yml是用户级的资源配置项
bootstrap.yml是系统级的，优先级更高。
springcloud会创建一个bootstrap context，作为spring应用的application context的父上下文，初始化的时候，bootstrap context
负责将外部源加载配置属性并解析配置，这两个上下文共享一个从外部获取的environment
bootstrap属性有高优先级，默认情况下，他们不会被本地配置覆盖，bootstrap context和application context有着不同的约定，所以
新增一个bootstrap.yml文件，保证bootstrap context和application context配置的分离。
要将client模块下的application.yml文件改为bootstrap.yml文件，这是很关键的。
因为bootstrap.yml是比application.yml文件先加载的，bootstrap.yml优先级高于application.yml

第四步：修改config-dev.yml配置提交到github中，比如加个变量age或者版本号version

第五步：主启动类：类ConfigClientMain3355

第六步：业务类

第七步：
测试：启动Config配置中心3344微服务并自测：http://config-3344.com:3344/master/config-prod.yml
启动3355作为客户端准备访问：http://localhost:3355/configInfo

成功实现了客户端3355访问springcloud_config_center_3344通过GitHub获取配置信息。

问题：问题随之而来，分布式配置的动态刷新问题
Linux运维修改GitHub上的配置文件内容做调整
刷新3344，发现ConfigServer配置中心立刻响应
刷新3355，发现ConfigClient客户端没有任何响应
3355没有变化除非自己重启或者重新加载
难道每次运维修改配置文件，客户端都需要重启？恶梦

四、config客户端之动态刷新
1.避免每次更新配置都要重启客户端微服务3355
2.动态刷新：
步骤：
（1）修改3355模块
（2）pom引入actuator监控：
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
（3）修改yaml，暴露监控端口
# 暴露监控端点
management:
  endpoints:
    web:
      exposure:
        include: "*"
（4）在业务类controller上加上注解：@RefreshScope，实现刷新功能。
（5）此时修改GitHub配置文件的内容---》访问3344---》访问3355：http://localhost:3355/configInfo，看看3355会不会改变
通过测试，发现3355没有改变。
（6）how：需要运维人员发送post请求，刷新3355：
必须是POST请求，curl -X POST "http://localhost:3355/actuator/refresh"
（7）再次：http://localhost:3355/configInfo，OK，成功实现了客户端3355刷新到最新配置内容。避免了服务重启。
跟运维的兄弟商量一下，改配置，发POST刷新一下（给配置中心的客户端发通知）
3.想想还有什么问题？
假设有多个微服务客户端3355、3366、3377、......
每个微服务都要执行一次post请求，手动刷新？
可否广播，一次通知，处处生效？
我们想大范围的自动刷新，求方法。

第十四章：消息总线 SpringCloud Bus
一、概述
上一讲解的加深和扩充，一言以蔽之：
分布式自动刷新配置功能，springcloud bus配合springcloud config使用，可以实现配置的动态刷新。
springcloud bus是什么？Bus支持两种消息代理：RabbitMQ和Kafka
能干嘛？springcloud bus是用来将分布式系统的节点与轻量级消息系统链接起来的框架。它整合了java的事件处理机制和消息中间件的功能。
springcloud bus目前只支持RabbitMQ和Kafka
springcloud bus能管理和传播分布式系统之间的消息，就像一个分布式执行器，可用于广播状态更改、事件推送等，也可以当做微服务之间的
通信通道。
为何被称为总线？
什么是总线：在微服务架构的系统中，通常会使用轻量级的消息代理来构建一个共用的消息主题，并让系统中所有微服务都连接上来。由于该
主题产生的消息会被实例监听和消费，所以称它为消息总线，在总线上的各个实例，都可以方便地广播一些需要让其他连接在该主题上的实例
都知道的消息。
基本原理：ConfigClient实例都监听MQ中同一个topic（默认是springcloud bus）。当一个服务刷新数据的时候，它会把这个信息放入到topic
中，这样其他监听同一topic的服务就能得到通知，然后去更新自身的配置。
二、RabbitMQ环境配置
三、springcloud bus动态刷新全局广播
1.必须先具备良好的RabbitMQ环境

2.演示广播效果，增加复杂度，再以3355为模板再制作一个3366
3.设计思想：
（1）利用消息总线触发一个客户端/bus/refresh，而刷新所有客户端的配置
（2）利用消息总线触发一个服务端ConfigServer的/bus/refresh端点，而刷新所有客户端的配置
图二的架构显然更加适合，图一不适合的原因如下：
(1)打破了微服务的职责单一性，因为微服务本身是业务模块，它本身不应该承担配置刷新的职责。
(2)破坏了微服务各节点的对等性。
(3)有一定的局限性，例如：微服务在迁移时，它的网络地址常常会发生变化，此时如果想要做到自动刷新，那就会增加更多的修改。

4.给springcloud_config_center_3344配置中心服务端增加消息总线支持
pom：添加消息总线RabbitMQ支持
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
yaml：
# 添加rabbitMQ相关配置
rabbitmq:
  host: 192.168.1.169
  port: 15672
  username: chenzejie
  password: chenzejie
# rabbitMQ相关配置，暴露bus刷新配置的端点
management:
  endpoints: # 暴露bus刷新配置的端点
  web:
    exposure:
      include: "bus-refresh"
5.给springcloud_config_client_3355客户端增加消息总线支持：
pom：添加消息总线RabbitMQ的支持
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
yaml：
# 添加rabbitMQ相关配置
rabbitmq:
  host: 192.168.1.169
  port: 15672
  username: chenzejie
  password: chenzejie
6.给springcloud_config_client_3366客户端增加消息总线支持：
7.测试：
（1）运维工程师修改GitHub上配置文件增加版本号
（2）发送post请求：一次发送，处处生效：curl -X POST "http://localhost:3344/actuator/bus-refresh"
（3）配置中心：http://config-3344.com:3344/config-test.yaml
（4）客户端：http://localhost:3355/configInfo,http://localhost:3366/configInfo，获取配置中心，发现都已经刷新了
（5）一次修改，广播通知，处处生效
8.一次修改，广播通知，处处生效。
四、springcloud bus动态刷新定点通知
1.不想全部通知，只想定点通知：只想通知3355，不想通知3366
2.简单一句话：
指定某一个具体实例生效而不是全部
公式：http://localhost:配置中心的端口号/actuator/bus-refresh/{destination}
/bus/refresh请求不再发送到具体的服务实例上，而是发给config server并通过destination参数类指定需要更新配置的服务或实例
3.案例：
以刷新运行在3355端口上的config-client为例，只通知3355，不通知3366
curl -X POST "http://localhost:3344/actuator/bus-refresh/cloud-config-client:3355"
4.通知总结：
