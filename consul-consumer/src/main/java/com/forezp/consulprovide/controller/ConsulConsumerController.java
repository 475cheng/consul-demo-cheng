package com.forezp.consulprovide.controller;

import com.forezp.consulprovide.service.FeignConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsulConsumerController {

    @Autowired
    private FeignConsumerClient feignConsumerClient;

    @GetMapping("/consumer")
    public String getProducer(){
        return feignConsumerClient.findServiceHi();
    }
}
