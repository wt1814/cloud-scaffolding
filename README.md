
<!-- TOC -->

- [1. 项目搭建](#1-项目搭建)
    - [1.1. 待解决](#11-待解决)
    - [1.2. 环境基础](#12-环境基础)
    - [1.3. Maven](#13-maven)
    - [1.4. 项目介绍](#14-项目介绍)
    - [1.5. 项目基础](#15-项目基础)
    - [1.6. 集成Mybatis](#16-集成mybatis)
    - [1.7. 集成Redis](#17-集成redis)
        - [1.7.1. 集成Redis](#171-集成redis)
        - [1.7.2. 集成redisson](#172-集成redisson)
        - [1.7.3. 集成Redis+Caffeine两级缓存](#173-集成rediscaffeine两级缓存)
    - [1.8. 集成consul](#18-集成consul)
    - [1.9. 集成网关gateWay、认证授权security](#19-集成网关gateway认证授权security)
    - [1.10. 分布式](#110-分布式)
        - [1.10.1. 分布式id](#1101-分布式id)
        - [1.10.2. 分布式事务](#1102-分布式事务)
        - [1.10.3. 分布式锁](#1103-分布式锁)

<!-- /TOC -->

# 1. 项目搭建  
&emsp; gitee地址：https://gitee.com/wt1814/cloud-scaffolding  
&emsp; 小子的笔记：http://www.wt1814.com/view/doc#/    
&emsp; 努力更新中，敬请期待...  

## 1.1. 待解决  
1. redis哨兵搭建：  
    1. 问题：出口ip问题  
    2. 代码解决：1).@SpringBootApplication干掉自动配置，2).RedisTemplate自动装配@Autowired(required = false)  
2. 配置中心选择问题，目前注册中心使用consul。  


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


## 1.4. 项目介绍  
1. 项目介绍  
    1. parent，父项目；  
    2. assembly，组件项目，包含网关、百度uid-generator、~~链路~~；  
    3. common，基础项目。
        * common-api基础工具类，需要@ComponentScan("com.wuw")扫描，xxx-api依赖common-api。
        * common-server，基础服务。  
    4. ucenter，用户项目
2. 项目配置介绍：  
    1. bootstrap.yml，项目启动配置  
    &emsp; application.yml配置文件引入其他的yml配置文件：https://blog.csdn.net/Zack_tzh/article/details/103728869?utm_term=yml%E5%8C%85%E5%90%AB%E5%8F%A6%E4%B8%80%E4%B8%AAyml&utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~all~sobaiduweb~default-0-103728869-null-null&spm=3001.4430  
    2. application.yml，系统级配置  
    3. application-dataSource，中间件配置  
    4. application-buiness.yml，业务配置  


## 1.5. 项目基础  
1. 格式：  
    1. 统一格式返回ApiResult  
    2. JSON不返回Null，两种方案：  
        1. https://blog.csdn.net/javaee520/article/details/117900370
        2. @JsonInclude(JsonInclude.Include.NON_NULL)  
    2. 日期格式化：fastJson的@JSONField （~~非jackson的@JsonFormat~~） 和 Spring的@DateTimeFormat    
    3. ~~数据相关~~  
        1. ~~格式化~~  
        2. ~~数据脱敏~~  
        3. ~~加密算法~~  
2. 工具  
    1. JavaBean
        1. BeanUtils：MapStuct：https://baijiahao.baidu.com/s?id=1710072420980854506&wfr=spider&for=pc    
        2. 参数校验：  
            1. https://blog.csdn.net/xnn_fjj/article/details/100603270  
            2. 结合统一异常处理  
            3. ~~普通Java项目使用Hibernate Validator手动校验Bean：https://www.icode9.com/content-1-1305959.html~~
    2. http
        1. restTemplate（个人喜好）  
            1. 注意：@ComponentScan("com.wuw")扫描RestTemplateConfig类
            2. 使用：https://blog.csdn.net/jinjiniao1/article/details/100849237
        2. http重试spring-retry：https://www.hangge.com/blog/com.wuw.doubleCache.cache/detail_2522.html  
3. 异常：  
    1. 自定义业务异常：（两种方案，看个人喜好）    
        * 若service层以javaBean返回，则需在service逻辑代码里抛出业务异常BusinessException；
        * 异常处理消耗性能，service可以直接以ApiResult返回。  
    2. 统一异常处理：https://blog.csdn.net/qq_49281137/article/details/121101543  
4. 日志（本项目整合了log4j2）  
    1. 日志整合log4j2  
        1. https://blog.csdn.net/qq_43842093/article/details/123027783
        2. 依赖冲突
        3. 日志性能：https://blog.csdn.net/qq_26323323/article/details/124741008  
        4. 多环境设置：  
            1. SpringBoot+log4j2.xml使用application.yml属性值 https://www.cnblogs.com/extjava/p/7553642.html  
            2. ~~maven-resources插件~~  
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


## 1.7. 集成Redis  
1. 前期redis未搭建成功，去掉redis相关自动装配。  
    1. @SpringBootApplication(exclude= {DataSourceAutoConfiguration.class,
       		RedisAutoConfiguration.class, // todo redis
       		RedissonAutoConfiguration.class
       })
    2. @ConditionalOnProperty(name = "spring.redis.sentinel.enable", havingValue = "true")
2. 目前使用redis单机模式。redis哨兵模式，哨兵之间注册的是内网地址，需解决出口ip问题。  
    
### 1.7.1. 集成Redis  
2. SpringBoot集成Redis：https://blog.csdn.net/hanxiaotongtong/article/details/122893310  
&emsp; https://blog.csdn.net/wl_honest/article/details/124171062  

### 1.7.2. 集成redisson
1. 哨兵模式redisson：https://blog.csdn.net/weixin_45973130/article/details/122383689  
2. 单机模式：  
3. 报错：READONLY You can‘t write against a read only replica  
&emsp; https://blog.csdn.net/qq_42818496/article/details/107838154  
4. Redisson看门狗失效：https://blog.csdn.net/nlcexiyue/article/details/120783519  

### 1.7.3. 集成Redis+Caffeine两级缓存  
1. caffeine：咖啡因，本地缓存之王。  
1. 集成：  
    1. https://blog.csdn.net/Trunks2009/article/details/123982910  
    &emsp; 1).集成了V3版本，可以设置过期时间；2).集成了分布式环境改造，解决了缓存一致性问题。  
    2. 项目com.wuw.double-cache  
    3. 两个不同项目集成：报错NoClassDefFoundError，maven依赖冲突。  
 2. 使用  

## 集成mq  


## 1.8. 集成consul  
1. springboot项目集成Consul配置中心的多环境方案：  
    * https://blog.csdn.net/DU87680258/article/details/111879755  


## 1.9. 集成网关gateWay、认证授权security  
1. 网关，spring cloud gateway集成spring cloud security统一认证、授权  
    * https://www.jianshu.com/p/fbabb8684dfd  
2. gateway集成feign   
    &emsp; openFeign 调用服务报错:No qualifying bean of type ‘org.springframework.boot.autoconfigure.http.HttpMessage   
    * https://blog.csdn.net/shehuinibingge/article/details/108470373   
    * https://www.pudn.com/news/628f8328bf399b7f351e7130.html  


## 1.10. 分布式
### 1.10.1. 分布式id
&emsp; 集成百度uid-generator  
1. 百度uid-generator：https://blog.csdn.net/qq_43690938/article/details/116712041  
2. 步骤：1. 集成uid-generator原有项目， 2. 基础项目common-api配置， 3. 业务项目使用。  
3. 编码：  
    1. UidGeneratorConfiguration 使用@MapperScan(value = "com.baidu.fsg.uid.worker.dao")扫描  
    2. 配置文件里mybatis.mapper-locations = classpath:/idmapper/*.xml,classpath:/mapper/*.xml扫描  


### 1.10.2. 分布式事务  


### 1.10.3. 分布式锁  
