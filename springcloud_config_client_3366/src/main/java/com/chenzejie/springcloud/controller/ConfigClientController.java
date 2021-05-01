package com.chenzejie.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigClientController {
    @Value("${config.info}")
    private String configInfo;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/configInfo")
    public String getConfigInfo() {
        System.out.println("server.Port = " +serverPort+"，configInfo的内容--->" + configInfo);
        return "server.Port = " +serverPort+"，configInfo的内容--->" + configInfo;
    }
}