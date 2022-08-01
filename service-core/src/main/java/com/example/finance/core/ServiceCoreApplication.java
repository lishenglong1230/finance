package com.example.finance.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Lishenglong
 * @Date: 2022/7/29 15:55
 */
@SpringBootApplication
@ComponentScan({"com.example.finance","com.example.common"})
public class ServiceCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class, args);
    }
}
