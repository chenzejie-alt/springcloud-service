package com.chenzejie.springcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextConfig {
    @Bean
//    @LoadBalanced // 因为provider是微服务提供者集群，所以需要使用@LoadBanlanced注解赋予RestTemplate负载均衡的能力
    // 这里使用的是Ribbon默认的负载均衡策略：轮询
    // Ribbon和Eureka整合后，Consumer可以直接调用服务而不用关心地址和端口号，且该服务还有负载均衡的能力了。
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
