springcloud config分布式配置中心
springcloud bus消息总线
服务配置：config，Nacos
服务总线：Bus，Nacos

springcloud config分布式配置中心：
一、概述：
1、分布式系统面临的问题：配置问题
微服务意味着要将单体应用中的业务拆分成一个个子服务，每个服务的粒度较小，因此系统中会出现大量的服务，由于每个服务
都需要必要的配置信息才能运行，所以一套集中式的、动态的配置管理设施是必不可少的。
springcloud提供了ConfigServer来解决这个问题，我们每一个微服务自己带着一个application.yaml，上百个配置文件的管理...
2、是什么
springcloud config为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供了一个
中心化的外部配置。
怎么玩？springcloud config分为服务端和客户端两部分。
服务端也称为分布式配置中心，他是一个独立的微服务应用，用来连接配置服务器并为客户端提供获取配置信息，加密/解密信息等访问接口。
客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。
配置服务器默认采用git来存储配置信息，这样有助于对环境配置进行版本管理，并且可以通过git客户端工具来方便地管理和访问配置内容。
3、能干嘛
集中管理配置文件；不同环境不同配置，动态化的配置更新，分环境部署比如：dev/test/prod/beta/release；
运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息；
当配置发生变动时，服务不需要重启即可感知到配置的变化并应用新的配置；将配置信息以REST接口的信息暴露：post，curl访问刷新即可。
4、与GitHub整合配置
由于springcloud config默认使用git来存储配置文件，也有其他方式，比如支持svn和本地文件，但是推荐git，而且使用的是http,https
访问的形式。
5、官网
https://cloud.spirng.io/spring-cloud-static/spring-cloud-config/2.2.1.RELEASE/reference/html/
二、config服务端配置与测试
在GitHub上新建一个名为springcloud-config的新Repository
由上一步获得刚新建的git地址：git@github.com:chenzejie-alt/springcloud-config.git
本地硬盘目录上新建git仓库并且clone
此时在本地盘符下：
新建module：springcloud_config_center_3344，它即为cloud的配置中心模块springcloud Center
pom
yaml
主启动类
window下修改hosts文件，增加映射
测试通过config微服务是否可以从GitHub上获取配置内容：
先启动eureka7001，再启动配置中心3344：http://config-3344.com:3344/master/config-dev.yaml
配置读取规则：
官网：
/{label}/{application}-{profile}.yml，例如：master分支：http://config-3344.com:3344/master/config-dev.yml
/{application}-{profile}.yml
/{application}/{profile}/[/{label}]
重要配置细节总结：
/{name}-{profiles}.yaml
/{label}-{name}-{profiles}.yaml
label：分支branch
name：服务名
profiles：环境(dev/test/prod)
成功实现了用springcloud config通过GitHub获取配置信息
建议使用： /{label}/{application}-{profile}.yaml




