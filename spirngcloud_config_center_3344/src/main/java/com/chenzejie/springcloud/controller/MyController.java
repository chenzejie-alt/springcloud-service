package com.chenzejie.springcloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @GetMapping(value = "/hello")
    public String hello() {
        return "hello你好";
    }
}
