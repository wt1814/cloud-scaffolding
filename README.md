
<!-- TOC -->

- [1. 项目搭建](#1-项目搭建)
    - [1.1. 待解决](#11-待解决)
    - [1.2. 环境基础](#12-环境基础)
    - [1.3. Maven](#13-maven)
    - [1.4. 项目组织](#14-项目组织)
        - [1.4.1. 项目介绍](#141-项目介绍)
        - [1.4.2. ~~项目Demo脚手架制作~~](#142-项目demo脚手架制作)
    - [1.5. 项目基础](#15-项目基础)
    - [1.6. 集成Mybatis](#16-集成mybatis)
    - [1.7. 集成ShardingSphere](#17-集成shardingsphere)
    - [1.8. 集成Redis](#18-集成redis)
        - [1.8.1. 集成Redis](#181-集成redis)
        - [1.8.2. 集成redisson](#182-集成redisson)
        - [1.8.3. 集成Redis+Caffeine两级缓存](#183-集成rediscaffeine两级缓存)
    - [1.9. 集成mq](#19-集成mq)
    - [1.10. SpringCloud](#110-springcloud)
        - [1.10.1. 集成nacos](#1101-集成nacos)
        - [1.10.2. 集成网关gateWay、认证授权security](#1102-集成网关gateway认证授权security)
        - [1.10.3. 集成Sentinel](#1103-集成sentinel)
        - [1.10.4. 集成链路](#1104-集成链路)
            - [1.10.4.1. 集成SkyWalking](#11041-集成skywalking)
            - [1.10.4.2. ~~集成Sleuth~~](#11042-集成sleuth)
    - [1.11. 分布式](#111-分布式)
        - [1.11.1. 分布式id](#1111-分布式id)
        - [1.11.2. 分布式事务](#1112-分布式事务)
        - [1.11.3. 分布式锁](#1113-分布式锁)
    - [1.12. elk](#112-elk)
    - [1.13. 工具](#113-工具)
        - [1.13.1. 定时任务xxl-job](#1131-定时任务xxl-job)
        - [1.13.2. 上传](#1132-上传)
            - [1.13.2.1. 上传服务器](#11321-上传服务器)
            - [1.13.2.2. OSS](#11322-oss)
        - [1.13.3. 导入导出](#1133-导入导出)
        - [1.13.4. 字典表](#1134-字典表)

<!-- /TOC -->

# 1. 项目搭建  
&emsp; git地址：https://gitee.com/wt1814/cloud-scaffolding  https://github.com/wt1814/cloud-scaffolding.git  
&emsp; 小子的笔记：http://www.wt1814.com/view/doc#/    
&emsp; 目前暴露的ip是真实ip，1核2G服务器，仅用来学习，误攻击。  
&emsp; 努力更新中，敬请期待...  

## 1.1. 待解决  
1. redis哨兵搭建：  
    1. 问题：出口ip问题  
    2. 代码解决：1).@SpringBootApplication干掉自动配置，2).RedisTemplate自动装配@Autowired(required = false)  
2. 待集成  
    1. 数据脱敏
    2. 字典表  
    1. 集成ShardingSphere  
    2. 集成elk  

## 1.2. 环境基础  
1. JDK1.8  
2. ip地址一般在服务器hosts中配置。（本项目中暂无使用）  

    ```xml
    <consul.host>wuw</consul.host>
    ```

## 1.3. Maven  
1. maven项目循环依赖，采用api与服务server分离接口形式搭建微服务。  
2. maven多模块项目。依赖的版本号一般在父项目parent中统一管理。  

    ```xml
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${spring-cloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-dependencies</artifactId>
                    <version>${spring-boot.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    ```
3. Maven设置多环境：https://www.likecs.com/show-204897168.html    
&emsp; 已经集成，如果environment下有同名文件，会覆盖。目前只用到consul地址多环境。  
4. 暂无maven私服，个人维护...  


## 1.4. 项目组织  
### 1.4.1. 项目介绍
1. 项目介绍  
    1. parent，父项目；  
    2. assembly，组件项目，包含网关、百度uid-generator、~~链路~~；  
    &emsp; 脚手架工程：cloud-scaffolding-demo。~~待制作：maven archetype。~~   
    3. common，基础项目。
        &emsp; common-api基础工具类，需要@ComponentScan("com.wuw")扫描，xxx-api依赖common-api。
        &emsp; common-server，基础服务。  
    4. ucenter，用户项目
2. 项目配置介绍：  
    1. bootstrap.yml，项目启动配置  
    &emsp; application.yml配置文件引入其他的yml配置文件：https://blog.csdn.net/Zack_tzh/article/details/103728869?utm_term=yml%E5%8C%85%E5%90%AB%E5%8F%A6%E4%B8%80%E4%B8%AAyml&utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~all~sobaiduweb~default-0-103728869-null-null&spm=3001.4430  
    2. application.yml，系统级配置  
    3. application-dataSource，中间件配置  
    4. application-buiness.yml，业务配置  


### 1.4.2. ~~项目Demo脚手架制作~~  
&emsp; 脚手架工程：cloud-scaffolding-demo。~~待制作：maven archetype。~~   

## 1.5. 项目基础  
1. 格式：  
    1. 统一格式返回ApiResult  
    2. JSON不返回Null，两种方案：  
        &emsp; 1).https://blog.csdn.net/javaee520/article/details/117900370  
        &emsp; 2).@JsonInclude(JsonInclude.Include.NON_NULL)  
    2. 日期格式化：fastJson的@JSONField （~~非jackson的@JsonFormat~~） 和 Spring的@DateTimeFormat    
    3. ~~数据相关~~  
        &emsp; 1).~~格式化~~  
        &emsp; 2).~~数据脱敏~~  
        &emsp; 3).~~加密算法~~  
2. 工具  
    1. JavaBean  
        &emsp; 1. BeanUtils：MapStuct：https://baijiahao.baidu.com/s?id=1710072420980854506&wfr=spider&for=pc    
        &emsp; 2. 参数校验：  
            &emsp; 1).https://blog.csdn.net/xnn_fjj/article/details/100603270  
            &emsp; 2).结合统一异常处理  
            &emsp; 3).~~普通Java项目使用Hibernate Validator手动校验Bean：https://www.icode9.com/content-1-1305959.html~~  
    2. http  
        &emsp; 1. restTemplate（个人喜好）  
            &emsp; 1).注意：@ComponentScan("com.wuw")扫描RestTemplateConfig类  
            &emsp; 2).使用：https://blog.csdn.net/jinjiniao1/article/details/100849237  
        &emsp; 2. http重试spring-retry：https://www.hangge.com/blog/com.wuw.doubleCache.cache/detail_2522.html  
3. 异常：  
    1. 自定义业务异常：（两种方案，看个人喜好）    
        &emsp; 1).若service层以javaBean返回，则需在service逻辑代码里抛出业务异常BusinessException；  
        &emsp; 2).异常处理消耗性能，service可以直接以ApiResult返回。   
    2. 统一异常处理：https://blog.csdn.net/qq_49281137/article/details/121101543  
4. 日志（本项目整合了log4j2）  
    1. 日志整合log4j2  
        &emsp; 1.https://blog.csdn.net/qq_43842093/article/details/123027783  
        &emsp; 2.依赖冲突  
        &emsp; 3.日志性能：https://blog.csdn.net/qq_26323323/article/details/124741008    
        &emsp; 4.多环境设置：  
            &emsp; &emsp; 1).SpringBoot+log4j2.xml使用application.yml属性值 https://www.cnblogs.com/extjava/p/7553642.html  
            &emsp; &emsp; 2).~~maven-resources插件~~   
    2. ~~logbcak异步输出：https://blog.csdn.net/qq_38536878/article/details/123821072~~   
    3. ~~日志切面记录请求~~   
5. 接口  
    1. RESTful风格  
    2. ~~接口幂等~~  
    3. ~~接口防刷/反爬虫~~  
    4. ~~接口安全~~
    5. ~~日志预警：1).日志框架预警；2). Filebeat+Logstash发送Email告警日志~~  

## 1.6. 集成Mybatis  
1. 整合druid： 
    * https://www.cnblogs.com/carry-huang/p/15260422.html  
    * https://blog.csdn.net/kobe_IT/article/details/123531088
    * logback配置Druid Filter：https://blog.csdn.net/qq_42145871/article/details/90704632
2. 集成mybatis：  
    * https://blog.csdn.net/jzman/article/details/111027453  
3. 集成mybatis-generator-core插件。（个人喜好。一直用的maven插件，不怎么喜欢idea或其他外置插件）  


## 1.7. 集成ShardingSphere
1. mySql读写分离实现  

2. 集成ShardingSphere  


## 1.8. 集成Redis  
1. 前期redis未搭建成功，去掉redis相关自动装配。  
    1. @SpringBootApplication(exclude= {DataSourceAutoConfiguration.class,
       		RedisAutoConfiguration.class, // todo redis
       		RedissonAutoConfiguration.class
       })
    2. @ConditionalOnProperty(name = "spring.redis.sentinel.enable", havingValue = "true")
2. 目前使用redis单机模式。redis哨兵模式，哨兵之间注册的是内网地址，需解决出口ip问题。  
    
### 1.8.1. 集成Redis  
&emsp; SpringBoot集成Redis：https://blog.csdn.net/hanxiaotongtong/article/details/122893310  
&emsp; https://blog.csdn.net/wl_honest/article/details/124171062  

### 1.8.2. 集成redisson
1. 哨兵模式redisson：https://blog.csdn.net/weixin_45973130/article/details/122383689  
2. 单机模式：  
3. 报错：READONLY You can‘t write against a read only replica  
&emsp; https://blog.csdn.net/qq_42818496/article/details/107838154  
4. Redisson看门狗失效：https://blog.csdn.net/nlcexiyue/article/details/120783519  

### 1.8.3. 集成Redis+Caffeine两级缓存  
1. caffeine：咖啡因，本地缓存之王。  
1. 集成：  
    1. https://blog.csdn.net/Trunks2009/article/details/123982910  
    &emsp; 1).集成了V3版本，可以设置过期时间；2).集成了分布式环境改造，解决了缓存一致性问题。  
    2. 项目com.wuw.double-cache  
    3. 两个不同项目集成：报错NoClassDefFoundError，maven依赖冲突。  
 2. 使用  

## 1.9. 集成mq  
1. 集成RocketMq（~~两主两从~~）。  
    1. 先搭建成功，创建一个topic，最后再在程序中集成。  
    2. 搭建：  
        &emsp; https://blog.csdn.net/qq_39280536/article/details/105020434
        &emsp; https://blog.csdn.net/moyuanbomo/article/details/115375785  
        &emsp; RocketMQ Web控制台监控界面介绍+部署 https://blog.csdn.net/abu935009066/article/details/120828337
    3. 部分问题：  
        &emsp; 1). /bin/runserver.sh和runbroker.sh设置堆大小  
        &emsp; 2). RocketMQ集群启动报错：java.lang.RuntimeException: Lock failed,MQ already started
        &emsp; 3). https://blog.csdn.net/TaylorSwiftiiln/article/details/121077705
    4. SpringBoot整合RocketMq：  
        &emsp; https://blog.csdn.net/qq_26154077/article/details/111013842  
        &emsp; https://blog.csdn.net/qq_43631716/article/details/119902582  


## 1.10. SpringCloud
### 1.10.1. 集成nacos 
1. springCloud集成nacos：https://blog.csdn.net/footless_bird/article/details/125362050  
2. maven多环境参考https://blog.csdn.net/DU87680258/article/details/111879755  


### 1.10.2. 集成网关gateWay、认证授权security  
1. 网关，spring cloud gateway集成spring cloud security统一认证、授权  
    * https://www.jianshu.com/p/fbabb8684dfd  
2. gateway集成feign   
    &emsp; openFeign 调用服务报错:No qualifying bean of type ‘org.springframework.boot.autoconfigure.http.HttpMessage   
    * https://blog.csdn.net/shehuinibingge/article/details/108470373   
    * https://www.pudn.com/news/628f8328bf399b7f351e7130.html  


### 1.10.3. 集成Sentinel
1. 搭建  
&emsp; https://sentinelguard.io/zh-cn/docs/dashboard.html    
2. 集成  
    1. 官方使用文档：https://sentinelguard.io/zh-cn/docs/basic-api-resource-rule.html  
    2. 普通接口和feign集成Sentinel  
    &emsp; https://www.jb51.net/article/226839.htm  
    &emsp; https://www.jianshu.com/p/f5cabdef0de1  
    &emsp; ~~https://blog.csdn.net/MenBad/article/details/125118367~~  
    3. ~~dubbo之使用sentinel限流~~  
    &emsp; https://blog.csdn.net/wang0907/article/details/121356872  
    4. gateway集成Sentinel https://blog.51cto.com/u_15284359/4874743  
    5. ~~dubbo之使用sentinel限流  https://blog.csdn.net/wang0907/article/details/121356872~~  
  

### 1.10.4. 集成链路
#### 1.10.4.1. 集成SkyWalking  
1. 集成SkyWalking  
&emsp; https://blog.csdn.net/weixin_35574537/article/details/112952419   
&emsp; Skywalking8：https://blog.csdn.net/Cy_LightBule/article/details/123855647  
&emsp; https://zhuanlan.zhihu.com/p/268913908  
&emsp; http://t.zoukankan.com/duanxz-p-15602842.html  

&emsp; 注意：  
&emsp; &emsp; 1. 要注意版本。  
&emsp; &emsp; 2. 要启动两个服务：startup.*:组合脚本，同时启动oapService.*，webappService.*脚本  
&emsp; &emsp; 3. 启动服务时VM参数：-javaagent:G:\software\apache-skywalking-java-agent-8.9.0\skywalking-agent\skywalking-agent.jar -Dskywalking.agent.service_name=consumer -Dskywalking.collector.backend_service=ip:11800    

2. ~~如何使用 SkyWalking 给 Dubbo 服务做链路追踪？~~  
&emsp; https://blog.csdn.net/XiaoHanZuoFengZhou/article/details/103287858?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-0-103287858-blog-120191083.pc_relevant_multi_platform_whitelistv2_ad_hc&spm=1001.2101.3001.4242.1&utm_relevant_index=3
    

#### 1.10.4.2. ~~集成Sleuth~~
1. Spring Cloud集成Sleuth  
2. dubbo服务整合zipkin  
&emsp; https://blog.csdn.net/zzqq12345/article/details/107187496  
&emsp; https://www.jb51.net/article/218105.htm  
  

## 1.11. 分布式
### 1.11.1. 分布式id
&emsp; 集成百度uid-generator  
1. 百度uid-generator：https://blog.csdn.net/qq_43690938/article/details/116712041  
2. 步骤：1. 集成uid-generator原有项目， 2. 基础项目common-api配置， 3. 业务项目使用。  
3. 编码：  
    1. UidGeneratorConfiguration 使用@MapperScan(value = "com.baidu.fsg.uid.worker.dao")扫描  
    2. 配置文件里mybatis.mapper-locations = classpath:/idmapper/*.xml,classpath:/mapper/*.xml扫描  


### 1.11.2. 分布式事务  


### 1.11.3. 分布式锁  
&emsp; 参考Redission章节  


## 1.12. elk  


## 1.13. 工具  
### 1.13.1. 定时任务xxl-job  
&emsp; https://blog.csdn.net/Fristm/article/details/125351356  
&emsp; https://blog.csdn.net/yunhaoyoung/article/details/120508147  
&emsp; docker安装  https://www.bbsmax.com/A/ZOJPNR6xdv/  
&emsp; https://www.cnblogs.com/ysocean/p/10541151.html#_label4_0  
&emsp; https://mp.weixin.qq.com/s/G6yGtDGyf3gASvUBTo6AEg  

### 1.13.2. 上传
#### 1.13.2.1. 上传服务器  


#### 1.13.2.2. OSS  
1. 阿里云oss  
    1. 公有桶   
    2. 私有桶   
        &emsp; 1. 上传  
            &emsp; &emsp; 使用STS临时访问凭证上传  
            &emsp; &emsp; https://help.aliyun.com/document_detail/100624.html?spm=5176.8466032.help.dexternal.739f1450FwuC0e  
            &emsp; &emsp; https://help.aliyun.com/document_detail/100624.html?spm=5176.8466032.policy.1.17ea1450MYk2w4  
        &emsp; 2. 下载  
            &emsp; &emsp; https://help.aliyun.com/document_detail/39607.htm?spm=a2c4g.11186623.0.0.72075d60lbnzq4#concept-39607-zh  
            &emsp; &emsp; 使用签名URL进行临时授权  
            &emsp; &emsp; https://help.aliyun.com/document_detail/32016.htm?spm=a2c4g.11186623.0.0.c8861c91aMITas#concept-32016-zh  
            &emsp; &emsp; STS临时授权OSS操作权限报“Access denied by authorizer's policy”错误  
            &emsp; &emsp; https://help.aliyun.com/document_detail/161911.html  
        &emsp; 3. 回源，CDN加速  
        &emsp; 4. 跨域：  
            &emsp; &emsp; https://help.aliyun.com/document_detail/31870.html?spm=5176.8466032.cors.1.527a1450z1AZq0  
            &emsp; &emsp; https://help.aliyun.com/document_detail/40183.html?spm=a2c4g.11186623.0.0.52af68bath8RRd  


### 1.13.3. 导入导出
1. 导出，合并单元格，使用CellRangeAddress硬拼。  

### 1.13.4. 字典表  
