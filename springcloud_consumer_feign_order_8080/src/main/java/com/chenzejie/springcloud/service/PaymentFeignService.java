package com.chenzejie.springcloud.service;

import com.chenzejie.springcloud.pojo.CommonResult;
import com.chenzejie.springcloud.pojo.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(value = "CLOUD-PAYMENT-SERVICE")
public interface PaymentFeignService {
    @GetMapping(value = "/payment/get/{id}")
    CommonResult<Payment> getPaymentById(@PathVariable("id") Integer id);

    @GetMapping(value = "/payment/feign/timeout")
    String paymentFeignTimeout();
}
