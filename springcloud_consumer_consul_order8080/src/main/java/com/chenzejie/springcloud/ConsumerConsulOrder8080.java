package com.chenzejie.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConsumerConsulOrder8080 {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerConsulOrder8080.class,args);
    }
}
