# consul-demo-cheng
本项目是springcloud-consul的demo，其中引用了Feign做负载均衡。  
服务器配置如下：  
172.20.3.91;172.20.3.92;172.20.3.93 这3台consul-server模式启动的服务端  
172.20.3.97 和 172.20.3.98 为两台consul-client模式启动的客户端  
consul-consumer注册到172.20.3.97，consul-producer注册到172.20.3.98  
其中一些注意事项请看springcloud-consul.md