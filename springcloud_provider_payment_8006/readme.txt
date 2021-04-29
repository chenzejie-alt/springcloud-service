what is consul？
consul是一套开源的分布式服务发现和配置管理系统，由HashiCorp公司用go语言开发
提供了微服务系统中的服务治理，配置中心，控制总线等功能，这些功能中的每一个都可以根据需要单独使用，也可以一起使用以构建
全方位的服务网格，总之consul提供了一种完整的服务网格解决方案。
他具有很多优点。包括：基于kaft协议，比较简洁，支持健康检查，同时支持http和DNS协议，支持跨数据中心的WAN集群，提供图形界面，
跨平台，支持linux，mac，windows。

consul能干嘛？
服务发现：提供http和DNS两种发现方式
健康监测：支持多种方式，http，TCP，Docker，Shell脚本定制化，
kv存储：key，value的存储方式
多数据中心：consul支持多数据中心
可视化web界面：

三个注册中心的异同点：
组件名：  语言      CAP    服务健康检查    对外暴露接口     springcloud
eureka：  java     AP     可配检查        HTTP            已集成
consul：  go       CP     支持            HTTP/DNS       已集成
zookeeper：java    CP     支持            客户端          已集成

CAP Theorem
CAP：
C:Consistency(强一致性)
A:Availability(可用性)
P:Partition tolerance(分区容错性)
CAP理论的核心是：一个分布式系统不可能同时很好的满足一致性，可用性和分区容错性这三个需求。
因此，根据CAP原理将NoSQL数据库分成了满足CA原则，CP原则和满足AP原则三大类。
CA：单点集群，满足一致性，可用性的系统，通常在可扩展性上不太强大。
CP：满足一致性，分区容错性的系统，通常性能不是特别高。consul，zookeeper
AP：满足可用性，分区容错性的系统，通常可能对一致性要求低一些。eureka
