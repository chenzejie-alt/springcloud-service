package com.chenzejie.springcloud.controller;

import com.chenzejie.springcloud.pojo.CommonResult;
import com.chenzejie.springcloud.pojo.Payment;
import com.chenzejie.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private DiscoveryClient discoveryClient; // 对于注册进eureka里面的微服务，可以通过服务发现来获得该服务的信息

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

    // 对注册进eureka里面的微服务，可以通过服务发现来获取该服务的信息
    // 因为我是微服务的提供者，所以我自己需要对外暴露自身微服务的信息：ip，端口，微服务名称
    @GetMapping(value = "/payment/discovery")
    public Object discovery() {
        // 通过discoveryClient可以获取eureka服务列表的信息，得到所有微服务名称组成的list集合
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            System.out.println("*****service = " + service);
        }
        // 获取一个微服务名称下的全部具体实例
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        // 获取每个实例的具体信息
        for (ServiceInstance instance : instances) {
            System.out.println(instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getPort() + "\t" + instance.getUri());
        }
        return this.discoveryClient;
    }
    /*
        *****service = cloud-payment-service
        *****service = cloud-order-service
        CLOUD-PAYMENT-SERVICE	192.168.230.1	8001	http://192.168.230.1:8001
        CLOUD-PAYMENT-SERVICE	192.168.230.1	8002	http://192.168.230.1:8002
    */

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout() {
        // 暂停三秒钟
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }

    @GetMapping(value = "/payment/lb")
    public String getPayment() {
        return serverPort;
    }
}
