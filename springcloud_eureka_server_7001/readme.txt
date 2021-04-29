（服务注册中心）---》（服务调用）---》（服务降级）---》（服务网关）---》（服务配置）---》（服务总线）
服务注册中心：eureka，zookeeper，consul，nacos
服务调用：Ribbon，LoadBalance
服务调用2：Feign，OpenFeign
服务降级：Hystrix，resilience5j，sentinel
服务网关：zuul，zuul2，gateway
服务配置：config，Nacos
服务总线：Bus，Nacos

eureka服务注册与发现：
eureka基础知识：
什么是服务治理：
springcloud封装了netflix公司开发的eureka模块来实现服务治理。
在传统的rpc远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，管理比较复杂，所以需要使用服务治理，管理服务与服务之间
依赖关系，可以使用服务调用，负载均衡，容错等，实现服务注册与发现。

什么是服务注册与发现：
eureka采用了CS(client and server)的设计架构，eureka server作为服务注册功能的服务器，他是服务注册中心。
而系统的其他微服务，使用eureka的客户端，连接到eureka server并维持心跳连接。
这样系统的维护人员就可以通过eureka server来监控系统中各个微服务是否正常运行。
在服务注册与发现中，有一个注册中心，当服务器启动的时候，会把当前自己服务器的信息 比如 服务地址通讯地址等以别名
方式注册到注册中心上。
另一方（消费者|服务调用者），以别名的方式去注册中心上获取到实际的服务通讯地址，然后再实现本地rpc调用。
rpc远程调用框架核心设计思想：在于注册中心，因为使用注册中心管理每个服务与服务之间的一个依赖关系（服务治理的概念）。
在任何rpc远程框架中，都会有一个注册中心（存放服务地址相关信息（接口地址））

eureka组件：
eureka的两个组件：eureka server 和 eureka client
eureka server提供服务注册服务：
各个微服务节点通过配置启动后，会在eureka server中进行注册，这样eureka server中的服务注册表中将会存储所有可用服务节点的
信息，服务节点的信息可以在界面中直观看到。
eureka client通过注册中心进行访问：
eureka client是一个java客户端，用于简化eureka server的交互，客户端同时也具备一个内置的、使用轮询（round-robin）负载
算法的负载均衡器。在应用启动后，将会向eureka server发送心跳（默认周期为30秒）。如果eureka server在多个心跳周期内没有接
收到某个节点的心跳，eureka server将会从服务注册表中把这个服务节点移除。（默认90秒）

单机eureka构建步骤：
建module：springcloud_eureka_server_7001
改pom：
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
写yaml：
主启动：@EnableEurekaServer 代表这是eureka服务注册中心
业务类：

集群eureka构建步骤：

actuator微服务信息完善：

服务发现discovery：

eureka自我保护：