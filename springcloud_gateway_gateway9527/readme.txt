服务注册中心：eureka，zookeeper，consul，nacos
服务调用：ribbon，LoadBalancer
服务调用2：Feign，OpenFeign
服务降级：Hystrix，resilience4j，sentinel
服务网关：zuul，zuul2，gateway
服务配置：config，Nacos
服务总线：Bus，Nacos
天上飞的理念必然有落地的实现。

接下来学习：zuul（路由网关），gateway（新一代网关）
netflix（zuul），gateway（spring）
gateway新一代网关：
一、概述简介：
1.gateway官网：
上一代zuul1.x：http://github.com/Netflix/zuul/wiki
当前gateway：http://cloud.spring.io/spring-cloud-static/spring-cloud-gateway/2.2.1.RELEASE/reference/html/
2.gateway是什么：
springcloud全家桶有一个重要的组件就是网关，在1.x版本中都是采用的zuul网关。
但在2.x版本中，zuul的升级一直跳票，springcloud最后自己研发了一个网关代替zuul，
那就是springcloud gateway，一句话：gateway是zuul1.x版本的替代。
gateway是在spring生态系统至上构建的API网关服务，基于spring5，springboot2和Project Reactor等技术。
gateway旨在提供一种简单而有效的方式来对API进行路由，以及提供一些强大的过滤器功能，例如：熔断、限流、重试等。
springcloud gateway是springcloud的一个全新项目，基于spring5.0+springboot2.0和project Reactor等技术开发的网关，它旨在
为微服务提供一种简单有效的统一的API路由管理方式。
springcloud gateway作为springcloud生态系统中的网关，目标是代替zuul，在springcloud2.0以上版本中，没有对新版本的zuul2.0
以上最新高性能进行集成，仍然还是使用的zuul1.x非Reactor模式的老版本，而为了提升网关的性能，springcloud gateway是基于webflux
框架而实现的，而webflux框架底层则使用了高性能的Reactor模式通信框架Netty。
springcloud gateway的目标提供了统一的路由方式且基于Filter链的方式提供网关基于的性能，例如：安全，监控/指标，和限流。
springcloud gateway使用的是webflux中的reactor-netty响应式编程组件，底层使用了netty通信框架。

3.gateway能干嘛：
反向代理，鉴权，流量控制，熔断，日志监控，...

4.微服务架构中网关在哪里：
5.我们为什么选择gateway？
netflix不太靠谱，zuul2.0一直跳票，迟迟不发布。
一方面因为zuul1.0已经进入了维护阶段，而且gateway是springcloud团队开发的，是亲儿子产品，值得信赖。
而且很多功能zuul都没有，用起来也非常简单便捷。
gateway是基于异步非阻塞模型上进行开发的，性能方面不需要担心，虽然netflix早就发布了最新的zuul2.x，但springcloud
貌似没有整合计划，而且netflix相关组件都宣布进入维护期，不知道前景如何？
多方面综合考虑gateway是很理想的网关选择。
6.springcloud gateway具有如下特性：
基于spring framework5，project Reactor和springboot2.0进行构建
动态路由：能够匹配任何请求属性；
可以对路由指定Predicate（断言）和Filter（过滤器）；
集成Hystrix的断路器功能；
集成springcloud服务发现功能；
易于编写的Predicate（断言）和Filter（过滤器）；
请求限流功能；
支持路径重写。

7.springcloud gateway和zuul的区别：
在springcloud Finchley正式版之前，springcloud推荐的网关是netflix提供的zuul。
(1)zuul1.x，是一个基于阻塞I/O的API Gateway
(2)zuul1.x基于servlet2.5使用阻塞架构它不支持任何长连接（如WebSocket），Zuul的设计模式和Nginx较像，每次I/O操作都是从工作线程中
选择一个执行，请求线程被阻塞到工作线程完成，但是差别是Nginx用C++实现，zuul用java实现，而jvm本身会有第一次加载较慢的情况，使得
zuul的性能相对较差。
(3)zuul2.x理念更加先进，想基于netty非阻塞和支持长连接，但springcloud目前还没有整合，zuul2.x的性能较zuul1.x有较大的提升。在性能
方面，根据官方提供的基准测试，springcloud gateway的RPS（每秒请求数）是zuul的1.6倍。
(4)springcloud gateway建立在spring framework5，Project Reactor和springboot2之上，使用非阻塞API。
(5)springcloud gateway还支持WebSocket，并且与spring紧密集成拥有更好的开发体验。

8.zuul1.x模型：
springcloud中所集成的zuul版本，采用的是tomcat容器，使用的是传统的servlet IO处理模型。
servlet的生命周期？servlet由servlet container进行生命周期管理。
container启动的时候构造servlet对象并调用servlet init()进行初始化；
container运行时接收请求，并为每个请求分配一个线程（一般从线程池中获取空闲线程）然后调用service()。
container关闭时调用servlet destroy()销毁servlet。
上述模式的缺点：servlet是一个简单的网络IO模型，当请求进入servlet container时，servlet container就会为其绑定一个线程，在并发
不搞的场景下这种模型是适用的，但是一旦高并发（比如用jmeter压），线程数量上涨，而线程资源代价是昂贵的，严重影响请求的处理时间，
在一些简单的业务场景下，不希望为每个request分配一个线程，只需要1个或者几个线程就能应对极大并发的请求，这种业务场景下servlet模型
没有优势。
所以zuul1.x是基于servlet之上的阻塞式处理模型，即spring实现了处理所有request请求的一个servlet（Dispatcher Servlet），并由
该servlet阻塞式处理模型，所以springcloud zuul无法摆脱servlet模型的弊端。

9.gateway模型：有了zuul怎么又出来gateway：
webflux是什么？http://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactor
传统的web框架，比如：struts2，springmvc等都是基于servlet API和servlet容器基础之上运行的。
但是在servlet3.1之后有了异步非阻塞的支持。而webflux是一个典型非阻塞异步的框架，它的核心是基于Reactor的相关API实现的。相对于
传统的web框架来说，他可以运行在诸如netty，undertow以及支持servlet3.1的容器上。非阻塞式+函数式编程（spring5必须让你使用java8）
spring webflux是spring5.0引入的新的响应式框架，区别于springmvc，他不需要依赖servletAPI，他是完全异步非阻塞的，并且基于Reactor
来实现响应式流规范。

二、三大核心概念：
Route：路由转发
路由是构建网关的基本模块，它由ID，目标URI，一系列的断言和过滤器组成，如果断言为true，则匹配该路由。
Predicate：断言
参考java8的java.util.function.Predicate，开发人员可以匹配http请求中的所有内容（例如请求头或者请求参数），如果请求与断言相匹配
则进行路由。
Filter：过滤
指的是spring框架中GatewayFilter的实例，使用过滤器，可以在请求在路由之前或者之前对路由进行修改。
总体：
web请求，通过一些匹配条件，定位到真正的服务节点，并在这个转发过程的前后，进行一些精细化控制。
predicate就是我们的匹配条件，而filter，就可以理解为一个无所不能的拦截器，有了这两个元素，再加上目标uri，就可以实现一个具体的路由

三、gateway工作流程
客户端向springcloud gateway发出请求，然后在gateway handler mapping中找到与请求相匹配的路由，将其发送到gateway web handler。
handler再通过指定的过滤器链来将请求发送到我们实际的服务执行业务逻辑，然后返回。
过滤器之间用虚线分开是因为过滤器可能会在发送代理请求之前（“pre”）或者请求之后（“post”）执行业务逻辑。
Filter在“pre”类型的过滤器可以做参数校验，权限校验，流量监控，日志输出，协议转换等。
在“post”类型的过滤器中可以做响应内容，响应头的修改，日志的输出，流量监控等有着非常重要的作用。
核心逻辑：路由转发+执行过滤器链

四、入门配置
新建module：springcloud_gateway_gateway9527
pom：
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
            <version>2.2.2.RELEASE</version>
        </dependency>
yaml：
业务类：
主启动类：
9527网关如何做路由映射
yaml新增网关配置
测试
yaml配置说明

gateway网关路由映射有两种配置方式：
第一种：在配置文件yaml中配置，具体见application.yaml文件
第二种：代码中注入RouteLocator的Bean

五、通过微服务名实现动态路由
默认情况下Gateway会根据注册中心注册的服务列表，以注册中心上微服务名为路径创建动态路由进行转发，从而实现动态路由的功能。
启动：一个eureka7001+两个微服务提供者8001/8002
pom：
yaml：需要注意的是uri的协议为lb，表示启动Gateway的负载均衡功能。
lb://serviceName是springcloud gateway在微服务中为我们自动创建的负载均衡uri
测试：http://localhost:9527/payment/lb     8001/8002切换
六、Predicate的使用
Predicate是什么？
springcloud gateway将路由匹配作为spring WebFlux HandlerMapping基础架构的一部分
springcloud gateway包括许多内置的Route Predicate工厂，所有这些Predicate都与http请求的不同属性匹配，多个Route Predicate
工厂可以进行组合。
springcloud gateway创建Route对象时，使用RoutePredicateFactory创建Predicate对象，Predicate对象可以赋值给Route，springcloud
Gateway包括许多内置的Route Predicate Factories

Route Predicate Factories这个是什么？
常用的Route Predicate
After Route Predicate
Before Route Predicate
Between Route Predicate
Cookie Route Predicate
Header Route Predicate
Host Route Predicate
Method Route Predicate
Path Route Predicate
Query Route Predicate
小总结：
七、Filter的使用
1.是什么？路由过滤器可用于修改进入的http请求和返回的http响应，路由过滤器只能指定路由进行过滤
springcloud gateway内置了多种路由过滤器，他们由gateway filter的工厂类来产生。
2.springcloud gateway的Filter
声明周期：pre和post
种类：GatewayFilter（31种之多）和GlobalFilter（10多个）
3.常用的Filter
4.自定义过滤器
自定义全局GlobalFilter：主要两个接口介绍，implements GlobalFilter，Ordered
能干嘛？全局日志记录，统一网关鉴权，...