四、Nacos作为服务配置中心演示：nacos代替服务配置中心config
（1）Nacos作为配置中心---基础配置
1.建工程：springcloud_alibaba_config_nacos_client_3377
2.pom：
        <!--nacos config-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
            <version>2.2.1.RELEASE</version>
        </dependency>
        <!--nacos discovery-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>2.2.1.RELEASE</version>
        </dependency>
3.yaml：
配置文件有两个：application.yaml和bootstra.yaml
为什么配置文件有两个？
Nacos和springcloud config一样，在项目初始化的时候，要保证先从配置中心进行配置拉取，拉取配置后，才能保证项目的正常启动。
springboot中配置文件的加载是存在优先级顺序的，bootstrap优先级高于application
bootstrap.yaml：
    # nacos配置
    server:
      port: 3377
    spring:
      application:
        name: nacos-config-client
      cloud:
        nacos:
          discovery:
            server-addr: localhost:8848 # nacos服务注册中心的地址
          config:
            server-addr: localhost:8848 # nacos作为配置中心的地址
            file-extension: yaml # 指定yaml格式的配置
    # ${spring.application.name}-{spring.profile.active}.${spring.cloud.nacos.config.file-extension}
application.yaml：
    spring:
      profiles:
        active: dev # 表示开发环境 test测试环境，prod产品环境
4.主启动：@EnableDiscoveryClient
5.业务类：
ConfigClientController
@RefreshScope // 支持nacos的动态刷新功能
通过springcloud原生注解@RefreshScope实现配置自动更新
5.在nacos中添加配置信息
Nacos中的匹配规则：理论，实操
理论：
Nacos中dataid的组成格式以及springboot配置文件中的匹配规则：${prefix}-${spring.profile.active}.${file-extension}
prefix默认为spring.application.name的值，也可以通过配置项spring.cloud.nacos.config.prefix来配置。
spring.profile.active即为当前环境对应的profile
file-extension为配置内容的数据格式，可以通过配置项spring.cloud.nacos.config.file-extension来配置。目前只支持properties和yaml类型。
官网：http://nacos.io/zh-cn/docs/quick-start-spring-cloud.html
最后公式：${spring.application.name}-{spring.profile.active}.${spring.cloud.nacos.config.file-extension}
# 配置中心配置文件：nacos-config-client-dev.yaml
实操：
配置新增：nacos-config-client-dev.yaml
6.Nacos界面配置对应：
设置DataId：公式：${spring.application.name}-${spring.profile.active}.${spring.cloud.nacos.config.file-extension}
历史配置：
7.测试：
启动前需要在nacos客户端-配置管理-配置管理栏目下有对应的yaml配置文件
运行cloud-config-nacos-client3377的主启动类
调用接口查看配置信息：http://localhost:3377/config/info
8.自带刷新功能
（2）Nacos作为配置中心---分类配置
问题：多环境多项目管理
问题1：实际开发中，通常一个系统会准备：
dev开发环境
test测试环境
prod开发环境
如何保证指定环境启动时服务能正确访问到nacos上相应环境的配置文件呢？

问题2：一个大型分布式微服务系统会有很多微服务子项目，每个微服务项目又都会有相应的开发环境，测试环境，预发环境，正式环境，......
那么怎么对这些微服务配置进行管理呢？
nacos的图形话管理界面
配置管理：dataId，group
命名空间：public（保留空间）
Namespace + Group + Data ID三者关系？为什么这么设置？
1.是什么？
类似java里面的package名和类名
最外层的namespace是可以用于区分部署环境的，Group和DataID逻辑上区分两个目标对象。
2.三者情况：
Namespace ---> Group ----> Service -----> Cluster1、Cluster2
默认情况：Namespace=public，Group=DEFAULT_GROUP，默认Cluster是DEFAULT
nacos默认的命名空间是public，Namespace主要用来实现隔离。
比方说我们现在有三个环境：开发，测试，生产环境，我们就可以创建三个Namespace，不同的Namespace之间是隔离的。
Group默认是DEFAULT_GROUP，Group可以把不同的微服务划分到同一个分组里面去。
Service就是微服务；一个微服务可以包含很多个Cluster（集群），nacos默认Cluster是DEFAULT，Cluster是对指定微服务的一个虚拟划分，
比方说为了容灾，将service微服务分别部署在了杭州机房和广州机房。
这时候就可以给杭州机房的Service微服务起一个集群名称：HZ
给广州机房的Service微服务起一个集群名称（GZ），还可以尽量让同一个机房的微服务互相调用，以提升性能。
最后是Instance，就是微服务实例。
Case：三种方案加载配置
1.DataID方案：指定spring.profile.active和配置文件的DataID来使不同环境下读取不同的配置
默认空间+默认分组+新建dev和test两个DataID：新建dev配置DataID，新建test配置DataID
通过spring.profile.active属性就能进行多环境下配置文件的读取。
测试：http://localhost:3377/config/info
配置是什么就加载什么
2.group方案：通过Group实现环境区分，新建group；在nacos图形界面控制台上面新建配置文件DataID；bootstrap+application：
在config下增加一条group的配置即可。可配置为DEV_GROUP或者TEST_GROUP
3.Namespace方案：新建dev/test的Namespace，回到服务管理-服务列表查看，按照域名配置填写，yaml
五、Nacos集群和持久化配置：（重要）
1.官网说明：
http://nacos.io/zh-cn/docs/cluster-mode-quick-start.html
官网架构图：
上图官网翻译，真实情况：
说明：按照上述，我们需要mysql数据库；
官网说明：
默认nacos使用嵌入式数据库实现数据的存储，所以，如果启动多个默认配置下的nacos节点，数据存储是存在一致性问题的，为了解决这个
问题，nacos采用了集中式存储的方式来支持集群部署，目前只支持mysql的存储。
nacos支持三种部署方式：
单机模式：用于测试和单机试用
集群模式：用于生产环境，确保高可用。
多集群模式：用于多数据中心场景
2.nacos持久化配置解释：
nacos默认自带的是嵌入式数据库derby：
http://github.com/alibaba/nacos/blob/develop/config/pom.xml
derby到mysql切换配置步骤：
启动nacos，可以看到是一个全新的空记录界面，以前是记录进derby
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
db.user=root
db.password=root
3.Linux版nacos+mysql生产环境配置
预计需要：1个Nginx+3个nacos注册中心+1个mysql
nacos下载linux版本
集群配置步骤：
（1）linux服务器上mysql数据库配置
（2）application.properties配置
（3）Linux服务器上nacos的集群配置cluster.conf
（4）编辑nacos的启动脚本startup.sh，使它能够接受不同的启动端口
（5）Nginx的配置，由它作为负载均衡器
（6）截止到此处，1个Nginx+3个nacos注册中心+1个mysql
测试：
高可用小结：
