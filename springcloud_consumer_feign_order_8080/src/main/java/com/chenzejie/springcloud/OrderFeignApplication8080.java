package com.chenzejie.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // 激活和开启feign
public class OrderFeignApplication8080 {
    public static void main(String[] args) {
        SpringApplication.run(OrderFeignApplication8080.class,args);
    }
}
