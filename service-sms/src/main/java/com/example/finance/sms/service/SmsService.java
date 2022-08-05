package com.example.finance.sms.service;

import java.util.Map;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/5 10:39
 */
public interface SmsService {
    void send(String mobile, String templateCode, Map<String, Object> param);
}
