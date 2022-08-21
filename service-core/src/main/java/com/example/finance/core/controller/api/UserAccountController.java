package com.example.finance.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.example.common.result.R;
import com.example.finance.base.util.JwtUtils;
import com.example.finance.core.hfb.RequestHelper;
import com.example.finance.core.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Api(tags = "会员账户")
@RestController
@RequestMapping("/api/core/userAccount")
@Slf4j
public class UserAccountController {
    @Resource
    private UserAccountService userAccountService;

    @ApiOperation("充值")
    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public R commitCharge(
            @ApiParam(value = "重置金额",required = true)
            @PathVariable BigDecimal chargeAmt, HttpServletRequest request){

        //获取当前登录的Id
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        //组装表单字符串，用于远程提交数据
        String formStr = userAccountService.commitCharge(chargeAmt, userId);
        return R.ok().data("formStr", formStr);
    }

    @ApiOperation(value = "用户充值异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户充值异步回调: " + JSON.toJSONString(paramMap));

        if (RequestHelper.isSignEquals(paramMap)){
            //判断业务是否成功
            if ("0001".equals(paramMap.get("resultCode"))){
                //用户数据同步
                return userAccountService.notify(paramMap);
            }else {
                return "success";
            }

        }else {
            return "fail";
        }

    }

    @ApiOperation("查询账户余额")
    @GetMapping("/auth/getAccount")
    public R getAccount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal account = userAccountService.getAccount(userId);
        return R.ok().data("account", account);
    }
}

