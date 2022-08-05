package com.example.finance.sms.service.impl;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.core.http.HttpClient;
import com.aliyun.core.http.HttpMethod;
import com.aliyun.core.http.ProxyOptions;
import com.aliyun.httpcomponent.httpclient.ApacheAsyncHttpClientBuilder;
import com.aliyun.sdk.service.dysmsapi20170525.models.*;
import com.aliyun.sdk.service.dysmsapi20170525.*;
import com.aliyuncs.CommonResponse;
import com.example.common.exception.Assert;
import com.example.common.exception.BusinessException;
import com.example.common.result.ResponseEnum;
import com.example.finance.sms.util.SmsProperties;
import com.google.gson.Gson;
import darabonba.core.RequestConfiguration;
import darabonba.core.client.ClientOverrideConfiguration;
import darabonba.core.utils.CommonUtil;
import darabonba.core.TeaPair;
import com.example.finance.sms.service.SmsService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/5 10:41
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Override
    public void send(String mobile, String templateCode, Map<String, Object> param) {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(SmsProperties.KEY_ID)
                .accessKeySecret(SmsProperties.KEY_SECRET)
                .build());

        // 创建远程客户端
        AsyncClient client = AsyncClient.builder()
                .region("cn-beijing") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                )
                .build();

        Gson gson = new Gson();
        String jsonParam = gson.toJson(param);

        // 创建请求对象
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName(SmsProperties.SIGN_NAME)
                .templateCode(templateCode)
                .phoneNumbers(mobile)
                .templateParam(jsonParam) //使用谷歌的json
                .build();

        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        SendSmsResponse resp = null;//获取响应数据
        try {
            resp = response.get();
            System.out.println("response.getData()" + new Gson().toJson(resp)); //获取响应数据
            String data = new Gson().toJson(resp);
            Map<String, Map<String, String>> resultMap = gson.fromJson(data, HashMap.class);
            System.out.println(resultMap);
            String code = resultMap.get("body").get("code");
            log.info("code ====== " + code);
            String message = resultMap.get("body").get("message");
            log.info("message ====== " + message);
//            String status_code = resp.getHeaders().get("Status Code");
//            System.out.println("Status======= "+status_code); 获取响应判断是否通信失败

            //Assert.notEquals("200",status_code, ResponseEnum.ALIYUN_RESPONSE_ERROR); //通信失败
            Assert.notEquals("isv.BUSINESS_LIMIT_CONTROL", code, ResponseEnum.ALIYUN_SMS_LIMIT_CONTROL_ERROR);
            Assert.equals("OK", code, ResponseEnum.ALIYUN_SMS_ERROR);
        } catch (InterruptedException e) {
            log.info("阿里云短信发送sdk调用失败" + e);
            //e.printStackTrace();
        } catch (ExecutionException e) {
            log.info("阿里云短信发送sdk调用失败" + e);
            //e.printStackTrace();
        }

        client.close();
    }
}
