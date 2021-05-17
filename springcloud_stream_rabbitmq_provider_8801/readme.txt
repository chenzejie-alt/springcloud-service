第十五章：springcloud stream消息驱动
一、消息驱动概述
1.springcloud stream是什么？
一句话：屏蔽底层消息中间件的差异，降低切换成本，统一消息的编程模型。
官网：http://spring.io/projects/spring-cloud-stream#overview
http://cloud.spring.io/spring-cloud-static/spring-cloud-stream/3.0.1.RELEASE/reference/html/
springcloud stream中文指导手册：http://m.wang1314.com/doc/webapp/topic/20971999.html
什么是springcloud stream？
官方定义springcloud stream是一个构建消息驱动微服务的框架。
应用程序通过inputs或者outputs来与springcloud stream中binder对象交互。
通过我们配置来binding（绑定），而springcloud stream的binder对象负责与消息中间件交互。
所以，我们只需要搞清楚如何与springcloud stream交互就可以方便地使用消息驱动的方式。
通过使用spring integration来连接消息代理中间件以实现消息事件驱动。
springcloud stream为一些供应商的消息中间件产品提供了产品化的自动化配置实现，引用了发布-订阅、消费组、分区的三个核心概念。
目前只支持RabbitMQ、Kafka。
springcloud stream是用于构建与共享消息传递系统连接的高度可伸缩的事件驱动微服务框架，该框架提供了一个灵活的编程模型，它建立
在已经建立和熟悉的spring熟语和最佳实践上，包括支持持久化的发布/订阅、消费组以及消息分区这三个核心概念。
2.设计思想
标准MQ：生产者/消费者之间靠消息媒介传递消息内容。Message
消息必须走特定的通道：消息通道MessageChannel
消息通道里的消息如何被消费呢，谁负责收发代理：消息通道MessageChannel的子接口SubscribableChannel，由MessageHandler消息处理器所订阅。
为什么用cloud stream？
比如说我们用到了RabbitMQ和Kafka，由于这两个消息中间件的架构上的不同，像RabbitMQ有exchange，kafka有topic和Partitions分区。
这些中间件的差异性导致我们实际项目开发给我们造成了一定的困扰，我们如果用了两个消息队列的其中一种，后面的业务需求，我们想要往
另外一种消息中间件进行迁移，这时候无疑就是一个灾难性的，一大堆东西都要重新推倒重新做，因为它跟我们的系统耦合了，这时候springcloud
stream给我们提供了一种解耦合的方式。
stream凭什么可以统一底层差异？
在没有绑定器这个概念的情况下，我们的springboot应用需要直接与消息中间件进行消息交互的时候，由于各消息中间件构建的初衷不同，他们
的实现细节上有较大的差异性，通过定义绑定器作为中间层，完美地实现了应用程序与消息中间件细节之间的隔离。
通过向应用程序暴露统一的Channel通道，使得应用程序不需要再考虑各种不同的消息中间件的实现。
通过定义绑定器作为中间层，实现了应用程序与消息中间件细节之间的隔离。
Binder：input对应于消费者，output对应于生产者。
stream对消息中间件的进一步封装，可以做到代码层面对中间件的无感知，甚至于动态的切换中间件（rabbitmq切换为kafka），使得微服务
开发的高度解耦，服务可以关注更多的自己的业务流程。
通过定义绑定器Binder作为中间层，实现了应用程序和消息中间件细节之间的隔离。
topic主题进行广播，在rabbitmq就是exchange，在kafka中就是topic。
stream中的消息通信方式遵循了发布-订阅模式：

3.springcloud stream标准流程套路
Binder：很方便地连接中间件，屏蔽差异。
Channel：通道，是队列Queue的一种抽象，在消息通讯系统中就是实现存储和转发的媒介，通过Channel对队列进行配置。
Source和Sink：简单的可理解为参照对象是springcloud stream自身，从stream发布消息就是输出，接收消息就是输入。

4.常用API和常用注解
Middleware：中间件，目前只支持RabbitMQ和Kafka
Binder：Binder是应用与消息中间件之间的封装，目前实现了Kafka和RabbitMQ的Binder，通过Binder可以很方便地连接中间件，可以动态地
改变消息类型（对应于Kafka的topic，RabbitMQ的exchange），这些都可以通过配置文件来实现。
@Input：注解标识输入通道，通过该输入通道接收到的消息进入应用程序。
@Output：注解标识输出通道，发布的消息将通过该通道离开应用程序。
@StreamListener：监听队列，用于消费者的队列的消息接收。
@EnableBinding：指通信通道channel和exchange绑定在一起。

二、案例说明
RabbitMQ环境已经ok
工程中新建三个模块：
springcloud_stream_rabbitmq_provider_8801，作为生产者进行发消息模块
springcloud_stream_rabbitmq_consumer_8802，作为消息接收模块
springcloud_stream_rabbitmq_consumer_8803，作为消息接收模块

三、消息驱动之生产者
新建module：springcloud_stream_rabbitmq_provider_8801
pom：
yaml：
主启动类：StreamMQMain8801
业务类：
发送信息接口，发送信息接口实现类，Controller
测试：启动eureka7001，启动rabbitmq，启动8801，访问http://localhost:8801/sendMessage
四、消息驱动之消费者
新建module：springcloud_stream_rabbitmq_consumer_8802
pom：
yaml：
主启动类StreamMQMain8802
业务类：
测试8001发送消息，8802接收消息：
五、分组消费与持久化
1.依照8802，clone出来一份8803：springcloud_stream_rabbitmq_consumer_8803
2.启动
rabbitmq
7701---服务注册
8801---消息生产
8802---消息消费
8803---消息消费
3.运行后有两个问题
第一个问题：有重复消费问题
第二个问题：消息持久化问题
4.消费
目前是8802、8803同时都收到消息了，存在重复消费的问题。
http://localhost:8801/sendMessage
如何解决？分组和持久化属性group
生产实际案例：
比如在以下场景中，订单系统我们做集群部署，都会从rabbitmq中获取订单信息，那如果一个订单同时被两个服务获取到，那么就会造成数据
错误，我们得避免这种情况。这时候我们就可以使用stream中的消息分组来解决。
注意在stream中处于同一group中的多个消费者是竞争关系，就能够保证消息只会被其中一个应用消费一次，不同组是可以全面消费的（重复消费）
5.分组
故障现象：重复消费；导致原因：默认分组group是不同的，组流水号不一样，被认为是不同组，可以消费。
自定义配置：
自定义配置分为同一组，解决重复消费问题。
原理：微服务应用放置于同一个group中，就能够保证消息只会被其中一个应用消费一次，不同组是可以重复消费的，同一个组内会发生竞争关系，
只有其中一个可以消费。
8802/8803都变成不同组，group两个不同：
group：chenzejieA，chenzejieB
8802修改yaml：group: chenzejieA
8803修改yaml：group: chenzejieB
分布式微服务应用中为了实现高可用负载均衡，实际上都会部署多个实例。
多数情况下，生产者发送消息给某个具体的微服务时只希望被消费一次，按照上面我们启动两个应用的例子，虽然他们同属于一个应用，但是这个
消息出现了被重复消费两次的情况，为了解决这个问题，springcloud stream提出了消费组的概念。
8802/8803实现了轮询分组，每次只有一个消费者
8801模块发的消息只能被8802或者8803其中一个接收到，这样避免了重复消费。
8802/8803都变成相同组，group两个相同。
group:chenzejieA，chenzejieA，8802修改yaml，8803修改yaml
同一个组的多个微服务实例，每次只会有一个实例拿到消息。
6.持久化
通过上述，解决了重复消费问题，再看看持久化
停止8802，8803并去除8802的分组group：chenzejieA
8803的分组group：chenzejieA没有去除。
8801先发送4条消息到rabbitmq
先启动8802，无分组属性配置，后台没有打出来消息
再启动8803，有分组属性配置，后台打出来了mq上的消息。



