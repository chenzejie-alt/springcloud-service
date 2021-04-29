package com.chenzejie.springcloud.service.impl;

import com.chenzejie.springcloud.service.PaymentHystrixService;
import org.springframework.stereotype.Service;

@Service
public class PaymentHystrixServiceImpl_Fallback implements PaymentHystrixService {
    @Override
    public String paymentInfo_OK(Integer id) {
        return "----PaymentHystrixServiceImpl_Fallback paymentInfo_OK，o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "----PaymentHystrixServiceImpl_Fallback paymentInfo_TimeOut，o(╥﹏╥)o";
    }
}
