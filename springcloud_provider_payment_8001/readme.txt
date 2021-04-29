eureka client端springcloud_provider_payment_8001将注册进eureka server成为服务提供者provider
改pom：
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
写yaml：
spring:
  application:
    name: cloud-payment-service
主启动：@EnableEurekaClient
测试：
自我保护机制


服务发现：Discovery
对于注册进eureka里面的微服务，可以通过服务发现来获得改服务的信息
修改springcloud_provider_payment_8001的Controller
8001主启动类
自测

eureka自我保护机制：
故障现象：
概念：保护模式主要用于一组客户端和eureka server之间存在网络分区场景下的保护，一旦进入保护模式，eureka server将会
尝试保护其服务注册表中的信息，不再删除服务注册表中的数据，也就是不会注销任何微服务。
如果在eureka server的首页看到以下这段提示，则说明eureka进入了保护模式。

EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN
THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.

导致原因：
某时刻某一个微服务不可用了，eureka不会立刻清理，依旧会对该微服务的信息进行保存。
属于ACP里面的AP分支
为什么会产生eureka自我保护机制？
为了防止eureka client可以正常运行，但是出现与eureka server网络不通的情况下，eureka server不会立刻将eureka client
服务剔除。
什么是自我保护模式？
默认情况下，如果eureka server在一定时间内没有接收到某个微服务实例的心跳，eureka会注销该实例（默认90秒），但是当网络分区
故障发生（延时、卡顿、拥挤）时，微服务与eureka server之间无法正常通信，以上行为看，可能变得非常危险了，因为微服务本身其实
是健康的，此时不应该注销这个微服务，eureka通过自我保护模式来解决这个问题，当eureka server节点在短时间内丢失过多客户端时，
可能发生了网络分区故障，那么这个节点就会进入自我保护模式。
eureka client                          eureka server
会员服务                                注册中心
会员服务启动的时候，把当前服务信息注册到eureka上。
自我保护机制：默认情况下eureka client定时向eureka server端发送心跳包，如果eureka在server端在一定时间内（默认90秒）没有
接收到eureka client发送心跳包，便会直接从服务注册列表中剔除该服务，但是在短时间（90秒中）丢失了大量的服务实例心跳，这时候
eureka server会开启自我保护机制，不会剔除该服务（该现象可能出现在如果网络不通但是eureka client为出现宕机，此时如果换别的
注册中心如果一定时间内没有接收到心跳将会剔除该服务，这样就出现了严重失误，因为客户端还能正常发送心跳，只是网络延迟问题，而
保护机制是为了解决此问题而出现的。）
在自我保护机制中，eureka server会保护服务注册列表中的信息，不会注销任何服务实例。
他的设计哲学就是宁可保留错误的服务注册信息，也不盲目地注销任何可能健康的服务实例。一句话讲解：好死不如赖活着
综上，自我保护模式是一种应对网络异常的安全保护措施，它的设计哲学是宁可同时保留所有微服务（健康的微服务和不健康的微服务都会
保留），也不盲目注销任何健康的微服务，使用自我保护模式，可以让eureka集群更加地健壮，稳定。

怎么禁止自我保护：
注册中心eureka server端7001：
出厂默认自我保护机制是开启的，eureka.server.enable-self-preservation=true
使用eureka.server.enable-self-preservation=false可以禁用自我保护模式
关闭效果
在eureka server端7001处设置关闭自我保护机制

生产者客户端eureka client端8001：
