package com.example.finance.core.controller.api;


import com.example.common.exception.Assert;
import com.example.common.result.R;
import com.example.common.result.ResponseEnum;
import com.example.common.util.RegexValidateUtils;
import com.example.finance.base.util.JwtUtils;
import com.example.finance.core.pojo.vo.LoginVO;
import com.example.finance.core.pojo.vo.RegisterVO;
import com.example.finance.core.pojo.vo.UserInfoVO;
import com.example.finance.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Api(tags = "会员接口")
@RestController
@RequestMapping("/api/core/userInfo")
@Slf4j
@CrossOrigin
public class UserInfoController {
    @Resource
    RedisTemplate redisTemplate;//RedisTemplate<String, String> redisTemplate; 不要用泛型 redis存的是json格式"xxxx" 必须强转String

    @Resource
    UserInfoService userInfoService;

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO) {
        String mobile = registerVO.getMobile();
        String password = registerVO.getPassword();
        String code = registerVO.getCode();

        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        Assert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        //校验验证码
        //String codeGen = redisTemplate.opsForValue().get("finance:sms:code:" + mobile);
        String codeGen = (String)redisTemplate.opsForValue().get("finance:sms:code:" + mobile);
        Assert.equals(code,codeGen, ResponseEnum.CODE_ERROR);

        //注册
        userInfoService.register(registerVO);
        return R.ok().message("注册成功");
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request){
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Assert.notEmpty(mobile,ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        String ip = request.getRemoteAddr();

        UserInfoVO userInfoVO = userInfoService.login(loginVO,ip);

        return R.ok().data("userInfo",userInfoVO);

    }

    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {
        //需要token在请求头当中携带过来
        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);
        if (result){
            return R.ok();
        }else {
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }


}

