package com.example.finance.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/5 15:08
 */

@SpringBootApplication
@ComponentScan({"com.example.finance", "com.example.common"})
public class ServiceOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class, args);
    }

}
