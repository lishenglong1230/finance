package com.example.finance.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.R;
import com.example.finance.core.pojo.entity.UserInfo;
import com.example.finance.core.pojo.query.UserInfoQuery;
import com.example.finance.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/10 9:46
 */
@Api(tags = "会员管理")
@RestController
@RequestMapping("/admin/core/userInfo")
@Slf4j
//@CrossOrigin
public class AdminUserInfoController {

    @Resource
    UserInfoService userInfoService;

    @ApiOperation("获取会员分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(value = "查询对象", required = false)//可以不查询 显示所有数据
                    UserInfoQuery userInfoQuery) {//get方式 不可以用RequestBody来传
        Page<UserInfo> pageParam = new Page<>(page, limit);
        //分页信息都会封装到IPage
        IPage<UserInfo> pageModel = userInfoService.listPage(pageParam, userInfoQuery);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation("锁定和解锁")
    @PostMapping("/lock/{id}/{status}")
    public R lock(
            @ApiParam(value = "用户id", required = true)
            @PathVariable("id") Long id,

            @ApiParam(value = "锁定状态（0：锁定 1：解锁）", required = true)
            @PathVariable("status") Integer status){

        //status传什么就改成什么
        userInfoService.lock(id,status);
        return R.ok().message(status==1?"解锁成功":"锁定成功");

    }

}
