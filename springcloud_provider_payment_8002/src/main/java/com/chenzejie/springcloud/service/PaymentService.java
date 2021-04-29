package com.chenzejie.springcloud.service;

import com.chenzejie.springcloud.pojo.Payment;

public interface PaymentService {
    int createPayment(Payment payment);
    Payment getPaymentById(Integer id);

}
