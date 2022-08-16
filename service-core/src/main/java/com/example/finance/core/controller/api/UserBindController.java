package com.example.finance.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.example.common.result.R;
import com.example.finance.base.util.JwtUtils;
import com.example.finance.core.hfb.RequestHelper;
import com.example.finance.core.pojo.vo.UserBindVO;
import com.example.finance.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Api(tags = "会员账号绑定")
@RestController
@RequestMapping("/api/core/userBind")
@Slf4j
public class UserBindController {

    @Resource
    UserBindService userBindService;


    @PostMapping("/auth/bind") //auth网关需要读取的路径片段 所有需要登录才能执行的功能写到auth下
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){

        //从header中获取token，并对token进行校验 确保用户已登录 ，并从token中提取userid
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        //根据userId做账户绑定,最终生成一个动态表单的字符串
        String formStr = userBindService.commitBindUser(userBindVO,userId);
        return R.ok().data("formStr",formStr);

    }

    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("账户绑定异步回调接受的参数如下： "+ JSON.toJSONString(paramMap));

        //验签
        if (!RequestHelper.isSignEquals(paramMap)){
            log.error("用户账户绑定异步回调签名验证错误： " + JSON.toJSONString(paramMap));
            return "fail";
        }

        log.info("验签成功!开始账户绑定");
        userBindService.notify(paramMap);

        return "success";

    }
}

