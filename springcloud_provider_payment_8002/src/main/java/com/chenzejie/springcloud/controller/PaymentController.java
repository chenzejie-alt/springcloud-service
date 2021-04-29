package com.chenzejie.springcloud.controller;

import com.chenzejie.springcloud.pojo.CommonResult;
import com.chenzejie.springcloud.pojo.Payment;
import com.chenzejie.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;
    @PostMapping(value = "/payment/create")
    public CommonResult create(@RequestBody Payment payment) {
        int result = paymentService.createPayment(payment);
        if (result > 0) {
            return new CommonResult(200,"插入数据库成功，server.port = " + serverPort,result);
        } else {
            return new CommonResult(444,"插入数据库失败",null);
        }
    }
    @GetMapping(value = "/payment/get/{id}")
    public CommonResult get(@PathVariable("id") Integer id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment != null) {
            return new CommonResult(200,"查询成功，server.port = " + serverPort,payment);
        } else {
            return new CommonResult(444,"没有对应记录，查询id：" + id,null);
        }
    }
    @GetMapping(value = "/payment/lb")
    public String getPayment() {
        return serverPort;
    }
}
