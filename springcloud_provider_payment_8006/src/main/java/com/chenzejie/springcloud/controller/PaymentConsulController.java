package com.chenzejie.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PaymentConsulController {
    @Value("${server.port}")
    private String serverPort;

    @RequestMapping(value = "/payment/consul")
    public String paymentconsul() {
        return "springcloud with consul：" + serverPort + "\t" + UUID.randomUUID().toString(); // UUID.randomUUID.toString是流水号
    }
}