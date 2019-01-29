package com.forezp.consulprovide.service;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

//在service-hi中启动 service-hi 和service-hii服务
@EnableFeignClients
@FeignClient("consul-producer")
@Service
public interface FeignConsumerClient {
    //这里写客户端的访问路径
    @RequestMapping("/hi")
    String findServiceHi();

}
