package com.example.finance.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Lishenglong
 * @Date: 2022/7/29 15:55
 */
@SpringBootApplication
@ComponentScan({"com.example.finance","com.example.common"})
public class ServiceSmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApplication.class, args);
    }
}
