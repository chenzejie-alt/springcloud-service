package com.chenzejie.springcloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlowLimitController {
    @GetMapping(value = "/testA")
    public String testA() {
//        try {
//            TimeUnit.MILLISECONDS.sleep(800);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return "-----testA";
    }
    @GetMapping(value = "/testB")
    public String testB() {
        return "-----testB";
    }

    @GetMapping(value = "/testD")
    public String testD() {
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        int age = 1/0;
        System.out.println("testD测试RT---平均响应时间");
        return "-----testD";
    }
}
