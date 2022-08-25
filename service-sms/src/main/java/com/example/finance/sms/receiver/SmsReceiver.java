package com.example.finance.sms.receiver;

import com.example.finance.base.dto.SmsDTO;
import com.example.finance.rabbitutil.constant.MQConst;
import com.example.finance.sms.service.SmsService;
import com.example.finance.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/25 16:23
 */
@Component
@Slf4j
public class SmsReceiver {
    @Resource
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            //队列名称、交换机、路由
            value = @Queue(value = MQConst.QUEUE_SMS_ITEM,durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS),
            key = {MQConst.ROUTING_SMS_ITEM}
    ))
    public void send(SmsDTO smsDTO){
        log.info("SmsReceiver消息监听");
        Map<String, Object> param = new HashMap<>();
        param.put("code",smsDTO.getMessage());
        smsService.send(smsDTO.getMobile(), SmsProperties.TEMPLATE_CODE,param);
    }
}
