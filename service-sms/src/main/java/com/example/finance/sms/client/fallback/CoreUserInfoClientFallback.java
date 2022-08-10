package com.example.finance.sms.client.fallback;

import com.example.finance.sms.client.CoreUserInfoClient;
import com.sun.org.apache.bcel.internal.generic.FADD;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/10 16:26
 */
@Service//一个真正的服务 代替远程失效服务
@Slf4j
public class CoreUserInfoClientFallback implements CoreUserInfoClient {

    @Override
    public boolean checkMobile(String mobile) {
        log.error("远程调用失效，服务熔断，服务降级(功能)");
        return false; //若远程失效 则直接返回false 手机未注册 跳过远程访问
    }
}
