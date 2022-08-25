package com.example.finance.rabbitutil.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/25 15:46
 */
@Service
@Slf4j
public class MQService {
    @Resource
    private AmqpTemplate amqpTemplate;

    public boolean sendMessage(String exchange, String routingkey, Object message) {
        log.info("发送消息");
        amqpTemplate.convertAndSend(exchange, routingkey, message);
        return true;
    }
}
