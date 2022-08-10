package com.example.finance.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.query.UserInfoQuery;
import com.example.finance.core.pojo.vo.LoginVO;
import com.example.finance.core.pojo.vo.RegisterVO;
import com.example.finance.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO, String ip);

    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id ,Integer status);

    Boolean checkMobile(String mobile);
}
