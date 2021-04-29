Feign是一个声明式WebService客户端，使用Feign能让编写Web Service客户端更加简单
它的使用方法是定义一个服务接口然后在上面添加注解，Feign也支持可拔插式的编码器和解码器。SpringCloud对Feign进行了封装，使其
支持了Spring MVC标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡。

Feign能干什么？
Feign使得编写java Http客户端变得更加容易。
前面在使用Ribbon+RestTemplate时，利用RestTemplate对http请求的封装处理，形成了一套模板化的调用方法，但是在实际开发中。
由于对服务依赖的调用不止一处。往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的
调用。
所以，Feign在此基础上做了进一步的封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需要创建一个接口
并使用注解的方式来配置它（以前是Dao接口上面标注Mapper注解，现在是一个微服务接口上面标注一个Feign注解即可。），即可完成对
服务提供方的接口的绑定，简化了使用Spring Cloud Ribbon时，自动封装服务调用客户端的开发量。

Feign集成了Ribbon
利用Ribbon维护了Payment的服务列表信息，并且通过轮询实现了客户端的负载均衡。而与Ribbon不同的是，通过Feign只需要定义服务绑定
接口并且以声明式的方法，优雅而简单地实现了服务调用。

Feign自带负载均衡配置项

Feign和OpenFeign两者的区别：
1.Feign是Spring Cloud组件中的一个轻量级RESTful风格的http服务客户端。Feign内置了Ribbon，用来做客户端负载均衡，去调用服务
注册中心的服务，Feign的使用方式是：使用Feign的注解定义接口，调用这个接口，就可以调用服务注册中心的服务。
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>

2.OpenFeign是spring cloud在Feign的基础上支持了springmvc的注解，如@RequestMapping等等。OpenFeign的@FeignClient可以
解析springmvc的@RequestMapping注解下的接口，并通过动态代理的方式产生实现类，实现类中做负载均衡并调用其他服务。
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

openFeign使用步骤：
接口+注解：微服务调用接口+@FeignClient
新建springcloud_consumer_feign_order_8080：Feign在消费端使用
改pom：
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
写yaml：
server:
  port: 8080
eureka:
  client:
    register-with-eureka: false
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/,http:eureka7002.com:7002/eureka/
主启动：@EnableFeignClients // 激活和开启feign
业务类：
业务逻辑接口+@FeignClient配置调用provider服务
新建PaymentFeignService接口并添加注解@FeignClient
控制层Controller调用PaymentFeignService
测试：
先启动2个eureka集群7001，7002
再启动2个微服务8001，8002
启动OpenFeign服务
Feign自带负载均衡配置项
小总结：

openFeign超时控制：
    超时设置，故意设置超时演示出错情况：
    服务提供方8001故意写暂停程序，服务消费方8080添加超时方法PaymentFeignService；
    服务消费方8080添加超时方法OrderFeignController，测试：http://localhost/consumer/payment/feign/timeout
    错误处理：
OpenFeign默认等待1秒钟，超时后报错。
默认Feign客户端只等待一秒钟，但是服务端处理需要超过1秒钟，导致Feign客户端不想等待了，直接返回报错。
为了避免这样的情况，有时候我们需要配置Feign客户端的超时控制。
yaml文件里需要开启OpenFeign客户端超时控制。

OpenFeign日志打印功能，日志增强：
Feign提供了日志打印功能，我们可以通过配置来调整日志级别，从而了解Feign中Http请求的细节。
说白了就是对Feign接口的调用情况进行监控和输出。
日志打印功能
是什么？
日志级别？
NONE：默认的，不显示任何日志
BASIC：仅记录请求方法，URL，响应状态码以及执行时间
HEADERS：除了BASIC中定义的信息之外，还有请求和响应的头信息
FULL：除了HEADERS中定义的信息之外，还有请求和响应的正文及元数据。
配置日志bean：
yaml文件里需要开启日志的Feign客户端
logging:
  level:
    # feign日志以什么级别监控哪个接口
    com.chenzejie.springcloud.service.PaymentFeignService: debug
后台日志查看