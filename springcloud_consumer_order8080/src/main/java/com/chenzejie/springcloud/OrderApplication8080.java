package com.chenzejie.springcloud;

import com.chenzejie.myrule.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration = MySelfRule.class) // 自定义负载均衡策略
public class OrderApplication8080 {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication8080.class,args);
    }
}
