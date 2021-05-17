springcloud alibaba入门简介
springcloud alibaba Nacos服务注册和配置中心
springcloud alibaba Sentinel实现熔断与限流
springcloud alibaba seata处理分布式事务

第十七章：springcloud alibaba入门简介
一、为什么会出现springcloud alibaba？
springcloud netflix项目进入了维护模式
二、springcloud alibaba带来了什么？
springcloud alibaba能干嘛？
服务限流降级：默认支持servlet，Feign，RestTemplate，Dubbo和RocketMQ限流降级功能的接入，可以在运行时通过控制台实时修改限流降级
规则，还支持查看限流降级Metrics监控。
服务注册与发现：适配springcloud服务注册与发现标准，默认集成了Ribbon的支持。
分布式配置管理：支持分布式系统中的外部化配置，配置更改时自动刷新。
消息驱动能力：基于springcloud stream为微服务应用构建消息驱动能力。
阿里云对象存储：阿里云提供的海量、安全、低成本、高可靠的云存储服务，支持在任何应用，任何时间，任何地点存储和访问任意类型的数据。
分布式任务调度：提供秒级，精准，高可靠，高可用的定时（基于Cron表达式）任务调度服务。同时提供分布式的任务执行模型，如网格任务。
网格任务支持海量子任务均匀分配到所有Worker上执行。
github：https://github.com/alibaba/spring-cloud-alibaba/blob/master/README-zh.md
怎么玩？
Sentinel：alibaba开源产品，把流量作为切入点，从流量控制，熔断降级，负载均衡保护等多个维度保护应用的稳定性。
Nacos：alibaba开源产品，一个更易于构建云原生应用的动态服务发现，配置管理和服务管理平台。
RocketMQ：Apache RocketMQ是基于java的高性能、高吞吐量的分布式消息和流计算平台
Dubbo：Apache Dubbo是一款高性能的java RPC框架。
Seata：alibaba开源产品，一个易于使用的高性能微服务分布式事务解决方案。
Alibaba Cloud OSS：阿里云对象存储服务，object storage service，简称oos，是阿里云提供的海量，安全，高可靠的云存储服务，您可以
在任何应用，任何时间，任何地点存储和访问任意类型的数据。
Alibaba Cloud ScheduleX：阿里中间件团队开发的一款分布式任务调度产品，支持周期性的任务与固定时间点触发任务。
官网：http://spring.io/projects/spring-cloud-alibaba#overview
Spring Cloud Alibaba为分布式应用程序开发提供了一站式解决方案。它包含开发分布式应用程序所需的所有组件，使您可以轻松地使
用Spring Cloud开发应用程序。
使用Spring Cloud Alibaba，您只需添加一些注释和少量配置即可将Spring Cloud应用程序连接到Alibaba的分布式解决方案，并使用
Alibaba中间件构建分布式应用程序系统。

第十八章：springcloud alibaba Nacos服务注册中心和配置中心
一、Nacos简介：
1.为什么叫做Nacos：前四个字母分别为Naming和Configuration的前两个字母，最后的s为Service
2.是什么？
一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。
Nacos：Dynamic Naming and Configuration Service
Nacos就是注册中心 + 配置中心的组合。等价于：Nacos = Eureka + Config + Bus
3.能干嘛？
替代eureka做服务注册中心：
替代config做服务配置中心：
4.去哪下？
http://github.com/alibaba/Nacos
官网文档：
spring-cloud-alibaba-group.github.io/github-pages/greenwich/spring-cloud-alibaba.html#_spring_cloud_alibaba_nacos_discovery
5.各注册中心比较：
服务注册与发现框架：  CAP框架      控制台管理        社区活跃度
Eureka              AP             支持           低（2.x版本闭源）
zookeeper           CP             不支持          中
consul              CP             支持            高
nacos               AP             支持            高
据说nacos在alibaba内部有超过10万的实例运行，已经过了类似双十一等各种大流量的考验
二、安装并运行Nacos：
本地java8+maven环境已经ok
先从官网下载Nacos
解压安装包，直接运行bin目录下的startup.cmd
命令运行成功后直接访问http://localhost:8848/nacos
结果页面：
三、Nacos作为服务注册中心演示：
官方文档：https://spring.io/projects/spring-cloud-alibaba；
https://spring-cloud-alibaba-group.github.io/github-pages/greenwich/spring-cloud-alibaba.html#_spring_cloud_alibaba_nacos_discovery
1.基于Nacos的服务提供者：
新建module：springcloud_alibaba_provider_payment_9001
pom：
父pom：
本模块pom：
yaml：
主启动：
业务类：
测试：http://localhost:9001/payment/nacos/1
nacos控制台
nacos服务注册中心，服务提供者9001都ok了。
为了下一章节演示nacos的负载均衡，参照9001新建9002：
2.基于Nacos的服务消费者：
新建module：springcloud_consumer_nacos_order_8083
pom：为什么nacos支持负载均衡？
yaml：
主启动：
业务类：ApplicationContextBean
OrderNacosController
测试：http://localhost:8083/consumer/payment/nacos/1
服务注册中心对比：
nacos全景图所示：
Nacos和CAP：
Nacos与其他注册中心特性对比：

                Nacos                        Eureka          Consul               CoreDNS         Zookeeper
一致性协议：     CP+AP                          AP              CP                   /                CP
健康检查： TCP/HTTP/Mysql/Client Beat        Client Beat       TCP/HTTP/gRPC/Cmd     /               Client Beat
负载均衡： 权重/DSL/metadata/CMDB              Ribbon          Fabio                 RR               /
雪崩保护：       支持                          支持             不支持               不支持            不支持
自动注销实例：   支持                          支持             不支持                不支持            支持
访问协议： Http/DNS/UDP                        HTTP            HTTP/DNS              DNS              TCP
监听支持：       支持                          支持             支持                 不支持            支持
多数据中心：     支持                          支持             支持                  不支持           不支持
跨注册中心：      支持                         不支持           支持                  不支持           不支持
springcloud集成： 支持                         支持            支持                  不支持           不支持
Dubbo集成：     支持                          不支持           不支持                不支持            支持
K8s集成：       支持                          不支持            支持                  支持            不支持
切换：Nacos支持AP和CP模式的切换
C是所有节点在同一时间看到的数据是一致的，而A的定义是所有的请求都会收到响应。
什么时候选择哪种模式？
如果不需要存储服务级别的信息且服务实例是通过nacos-client注册，并能够保持心跳，那么就可以选择AP模式。
当前主流的服务如springcloud和dubbo服务，都适合于AP模式，AP模式为了服务的可用性而减弱了一些一致性，因此AP模式下只支持
注册临时实例。
如果需要在服务级别编辑或者存储配置信息，那么CP是必须，K8S服务和DNS服务则适用于CP模式。
CP模式下则支持持久化实例，此时则是已Raft协议为集群运行模式，该模式下注册实例之前必须先注册服务，如果服务不存在，则会返回错误。
curl -X PUT '$NACOS_SERVER:8848/nacos/v1/ns/operator/switches?entity=serverMode&value=CP'
