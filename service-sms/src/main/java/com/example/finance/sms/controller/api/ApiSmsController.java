package com.example.finance.sms.controller.api;

import com.example.common.exception.Assert;
import com.example.common.result.R;
import com.example.common.result.ResponseEnum;
import com.example.common.util.RandomUtils;
import com.example.common.util.RegexValidateUtils;
import com.example.finance.sms.client.CoreUserInfoClient;
import com.example.finance.sms.service.SmsService;
import com.example.finance.sms.util.SmsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.RegEx;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/5 11:10
 */
@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
//@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {

    @Resource
    SmsService smsService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    CoreUserInfoClient coreUserInfoClient;

    @ApiOperation("获取验证码")
    @GetMapping("/send/{phone}")
    public R send(
            @ApiParam(value = "手机号",required = true)
            @PathVariable String phone){

        //手机号校验
        Assert.notEmpty(phone, ResponseEnum.MOBILE_NULL_ERROR);
        //校验手机号是否合法
        Assert.isTrue(RegexValidateUtils.checkCellphone(phone), ResponseEnum.MOBILE_ERROR);

        //判断手机号是否已注册
        boolean result = coreUserInfoClient.checkMobile(phone);
        log.info(result?"已注册":"未注册");
        Assert.isTrue(result==false, ResponseEnum.MOBILE_EXIST_ERROR);

        //组装短信模板参数
        Map<String ,Object> map = new HashMap<>();
        String code = RandomUtils.getFourBitRandom();//生成随机验证码
        map.put("code",code);

        //调用aliyun发送验证码
        //smsService.send(phone, SmsProperties.TEMPLATE_CODE,map);

        //验证码存redis
        //放到验证码下面 防止存入redis验证码却没发送
        redisTemplate.opsForValue().set("finance:sms:code:" + phone,code,5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }
}
