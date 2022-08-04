package com.example.finance.sms.util;

import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/4 17:00
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsProperties implements InitializingBean {
    /*
    region-id: cn-beijing
    key-id: LTAI5t6u9Q1DBFwYQkRVm4Fc
    key-secret: h5hb220jYEQNGsU9dE70Yohxmsw3mr
    template-code: SMS_154950909
    sign-name: 阿里云短信测试
    */
    //spring初始化之后 自动匹配驼峰命名
    private String regionId;
    private String keyId;
    private String keySecret;
    private String templateCode;
    private String signName;

    public static String REGION_Id;
    public static String KEY_ID;
    public static String KEY_SECRET;
    public static String TEMPLATE_CODE;
    public static String SIGN_NAME;

    //当私有成员被赋值后，此方法自动被调用，从而初始化常量
    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_Id = regionId;
        KEY_ID = keyId;
        KEY_SECRET = keySecret;
        TEMPLATE_CODE = templateCode;
        SIGN_NAME = signName;
    }
}
