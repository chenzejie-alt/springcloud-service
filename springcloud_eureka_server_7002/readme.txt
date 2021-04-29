集群eureka构建步骤：
eureka集群原理说明：
服务注册：将服务信息注册到注册中心
服务发现：从注册中心上获取服务信息
实质：存key服务名 取value调用地址
1.先启动eureka注册中心
2.启动服务提供者payment支付服务
3.支付服务启动后会把自身信息（比如服务地址以别名方式注册进eureka）
4.消费者order服务在需要调用接口时，使用服务别名去注册中心获取实际的rpc远程调用地址
5.消费者获得调用地址后，底层实际是利用HttpClient技术实现远程调用
6.消费者获得服务地址后会缓存在本地jvm内存中，默认每间隔30秒更新一次服务调用地址
问题：微服务rpc远程服务调用最核心的是什么？
高可用，试想你的注册中心只有一个，就容易出现单点故障，导致整个微服务环境不可用。
解决办法：搭建eureka注册中心集群，实现负载均衡+故障容错

eureka server集群环境构建步骤：
参考springcloud_eureka_server_7001
新建springcloud_eureka_server_7002
改pom
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
修改映射配置
写yaml（以前单机）：7001，7002

server:
  port: 7001
# eureka服务端的实例名称
eureka:
  instance:
    # eureka服务端的实例名称
    hostname: eureka7001.com
  client:
    # false表示不向注册中心注册自己
    register-with-eureka: false
    # false表示自己就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      # 设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone: http://eureka7002.com:7002/eureka/

# 多个注册中心互相注册，相互守望
server:
  port: 7002
# eureka服务端的实例名称
eureka:
  instance:
    hostname: eureka7002.com
  client:
    # false表示不向注册中心注册自己
    register-with-eureka: false
    # false表示自己就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      # 设置与eureka Server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone: http://eureka7001.com:7001/eureka/
      # 注册中心有多个，需要互相注册，eureka7002要注册eureka7001

主启动：@EnableEurekaServer

将支付服务8001微服务发布到上面2台eureka集群配置中
将订单服务8080微服务发布到上面2台eureka集群配置中
测试01
支付服务提供者8001集群环境构建
负载均衡
测试02
