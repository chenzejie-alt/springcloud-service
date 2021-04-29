eureka client端springcloud_consumer_order8080将注册进eureka server成为服务消费者consumer
改pom：
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
写yaml：
spring:
  application:
    name: cloud-order-service
主启动：@EnableEurekaClient
测试：

ribbon负载均衡服务调用

概述：
springcloud ribbon是基于netflix ribbon实现的一套客户端（消费端）负载均衡工具。
简单地说，ribbon是netflix发布的开源项目，主要功能是提供客户端的负载均衡算法和服务调用。ribbon客户端组件提供一系列的配置
项，如：连接超时，重试等。简单地说，就是在配置文件中列出LoadBalancer（简称LB）后面的所有机器，Ribbon会自动地帮助你基于
某种规则（如简单轮询，随机连接等）去连接这些机器。我们很容易使用ribbon实现自定义的负载均衡算法。

ribbon能干什么？

LoadBalanced（负载均衡）是什么？
简单地说就是将用户的请求平摊的分配到多个服务上，从而达到系统的HA（high availability）高可用。
常见的负载均衡有软件Nginx，LVS，硬件F5等。

Ribbon本地负载均衡客户端和Nginx服务端负载均衡的区别？
Nginx是服务器负载均衡，客户端所有请求都会交给Nginx，然后由Nginx实现转发请求，即负载均衡是由服务端实现的。
Nginx是集中式负载均衡。即在服务的消费方和提供方之间使用独立的LB实施（可以是硬件，如F5，也可以是软件，如Nginx），由该设施
负责将访问请求通过某种策略转发到服务的提供方。

ribbon本地负载均衡，在调用微服务接口的时候，会在注册中心上获取注册信息服务列表之后缓存到jvm本地，从而在本地实现rpc远程服务
调用技术。ribbon是进程内负载均衡，进程内LoadBalanced，将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后
自己再从这些地址中选择出一个合适的服务器。ribbon属于进程内负载均衡，他是一个类库，集成于消费方进程，消费方通过它来获取到服务
提供方的地址。

ribbon+restTemplate实现负载均衡远程服务调用。

ribbon负载均衡演示：
架构说明：总结：ribbon其实就是一个软负载均衡的客户端组件，它可以和其他所需请求的客户端结合使用，和eureka结合只是其中的一个实例。
ribbon在工作时分两步：
第一步：先选择eureka server，它优先选择在同一区域内负载较少的server
第二步：再根据用户指定的策略，在从server取到的服务注册列表中选择一个地址。其中ribbon提供了多种策略：比如轮询、随机和根据响应时间
加权。

ribbon核心组件IRule：
IRule：根据特点算法中从服务列表中选取一个要访问的服务。

ribbon自带的常见负载均衡策略/ribbon负载均衡算法：
com.netflix.loadBalancer.RoundRobinRule：轮询
com.netflix.loadBalancer.RandomRule：随机
com.netflix.loadBalancer.RetryRule：先按照RoundRobinRule的策略获取服务，如果获取服务失败则在指定时间内会进行重试，获取可用的服务。
WeightedResponseTimeRule：对RoundRobinRule的扩展，响应速度越快的实例选择权重越大，越容易被选择。
BestAvailableRule：会先过滤掉由于多次访问故障而处于断路器跳闸转态的服务，然后选择一个并发量最小的服务。
AvailabilityFilteringRule：先过滤掉故障实例，再选择并发较小的实例。
ZoneAvoidanceRule：默认规则，复合判断server所在区域的性能和server的可用性选择服务器。

如何替换：
修改springcloud_consumer_order_8080：
注意配置细节：
新建package
上面包下新建MySelfRule规则类：
主启动类添加@RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration = MySelfRule.class) // 自定义负载均衡策略
测试：

原理：
ribbon负载均衡算法：rest接口第几次请求数%服务器集群总数量=实际调用服务器位置下标，每次服务重启后rest接口计数从1开始。
List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
如：List[0] instances = 127.0.0.1:8002
List[1] instances = 127.0.0.1:8001
8001 + 8002 组合成为集群，他们共计2台机器，集群总数为2，按照轮询算法原理：
当请求数为1时：1%2=1对应下标位置为1，则获得服务地址为127.0.0.1:8001
当请求数为2时：2%2=0对应下标位置为0，则获得服务地址为127.0.0.1:8002
当请求数为3时：3%2=1对应下标位置为1，则获得服务地址为127.0.0.1:8001
当请求数为4时：4%2=0对应下标位置为0，则获得服务地址为127.0.0.1:8002
当请求数为5时：5%2=1对应下标位置为1，则获得服务地址为127.0.0.1:8001
以此类推。。。

源码：
手写：手写一个负载均衡算法，原理+JUC(CAS+自旋锁的复习)
1.ApplicationContextBean去掉注解@LoadBalanced
2.LoadBalancer接口
3.MyLB
4.OrderController
5.测试