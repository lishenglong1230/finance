package com.example.finance.core.service;

import com.example.finance.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface UserBindService extends IService<UserBind> {

    String commitBindUser(UserBindVO userBindVO, Long userId);

    void notify(Map<String, Object> paramMap);
}
