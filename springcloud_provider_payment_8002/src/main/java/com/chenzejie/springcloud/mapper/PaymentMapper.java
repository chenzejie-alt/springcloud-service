package com.chenzejie.springcloud.mapper;

import com.chenzejie.springcloud.pojo.Payment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMapper {
    int createPayment(Payment payment);
    Payment getPaymentById(@Param("id") Integer id);
}
