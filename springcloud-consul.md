>springcloud consul 服务注册发现 [consul 官方文档](https://www.consul.io/docs/index.html)

# consul 
每个节点都需要运行agent，他有两种运行模式server和client。每个数据中心官方建议需要3或5个server节点以保证数据安全，同时保证server-leader的选举能够正确的进行。
下面的前3条命令构建了 consul集群，第四条命令表示向consul集群中注册一个client节点
consul agent -server -bootstrap-expect 1 -data-dir /tmp/consul -node=s1 -bind=172.20.4.98 -ui -client 0.0.0.0  
consul agent -server -bootstrap-expect 3 -data-dir /tmp/consul -node=s2 -bind=192.168.87.208 -ui -client 0.0.0.0 -join 172.20.4.98
consul agent -server -bootstrap-expect 3 -data-dir /tmp/consul -node=s3 -bind=172.20.12.7 -ui -client 0.0.0.0 -join 172.20.4.98
consul agent -data-dir /tmp/consul -node=c1 -bind=192.168.87.208 -client=192.168.87.208 -join 172.20.12.7
单节点启动：consul agent -dev  -config-dir=/data/consul -data-dir /tmp/consul -node=s1 -bind=172.20.4.98 -ui -client 0.0.0.0
            consul agent -server -data-dir /data/service/consul -bootstrap-expect 1 -advertise 172.20.4.98  -client 0.0.0.0 -ui
            export PROMDASH_PATH_PREFIX="/consull"
-node：节点的名称  
-bind：绑定的一个地址，用于节点之间通信的地址，可以是内外网，必须是可以访问到的地址  
-server：这个就是表示这个节点是个SERVER  
-config-dir: 配置文件目录包括acl，tls等等
-bootstrap-expect：这个就是表示期望提供的SERVER节点数目，数目一达到，它就会被激活，然后就是内部选举LEADER了
-data-dir：表示持久化服务注册信息，集群信息的目录
-join：这个表示启动的时候，要加入到哪个集群内，这里就是说要加入到节点1的集群  
-client：客户端的ip地址，如果是server节点可以是0.0.0.0  
查看集群节点命令 consul members  ;查看集群节点状态 consul operator raft list-peers  

# 注意事项
1.只有2个server的时候，一个挂掉，不会选举出新的leader。建议3或5台，3台中如果leader挂掉，会在另外2台中重新选举leader
2.使用 -bootstrap 可以直接启动为leader，这和-bootstrap-expect 是有区别的
3.节点数据直接没有同步 （与Eureka不同，它不会数据同步，可以通过每台linux启动一个consul-client节点，然后再启动一个服务注册到本机节点）
4.Consul使用访问控制列表（ACL）来保护UI，API，CLI，服务通信和代理通信
     命令：consul acl bootstrap 生成token
      "acl": {
         "enabled": true,
         "default_policy": "deny",
         "down_policy": "extend-cache"
       },
5.备份数据命令：consul snapshot save backup.snap
  还原数据命令：consul snapshot restore backup.snap
# spirngcloud引入consul
以springboot为1.5.19为例
1. maven配置
```pom.xml
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.19.RELEASE</version>
        <relativePath/>
</parent>
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
</dependencies>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Edgware.SR3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```  

2. application.yml配置
```application.yml
spring:
  application:
    name: consul-producer
  cloud:
    consul:
      host: 172.20.4.98                 //默认值是localhost 
      port: 8500
      discovery:
        instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${server.port}
        health-check-path: /health          //注意consul需要健康检查的接口  这里引用actuator自带的/health 接口 如果是2.0的springboot 应该是actuator/health
        health-check-interval: 10s

server:
  port: 8502

management:
  security:
    enabled: false

```  

3. springboot的启动类要加入注解 @EnableDiscoveryClient