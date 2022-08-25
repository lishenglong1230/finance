package com.example.finance.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.exception.Assert;
import com.example.common.result.ResponseEnum;
import com.example.common.util.MD5;
import com.example.finance.base.util.JwtUtils;
import com.example.finance.core.mapper.UserAccountMapper;
import com.example.finance.core.mapper.UserLoginRecordMapper;
import com.example.finance.core.pojo.entity.UserAccount;
import com.example.finance.core.pojo.entity.UserInfo;
import com.example.finance.core.mapper.UserInfoMapper;
import com.example.finance.core.pojo.entity.UserLoginRecord;
import com.example.finance.core.pojo.query.UserInfoQuery;
import com.example.finance.core.pojo.vo.LoginVO;
import com.example.finance.core.pojo.vo.RegisterVO;
import com.example.finance.core.pojo.vo.UserIndexVO;
import com.example.finance.core.pojo.vo.UserInfoVO;
import com.example.finance.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import sun.security.krb5.internal.PAData;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    UserAccountMapper userAccountMapper;
    @Resource
    UserLoginRecordMapper userLoginRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {
        //判断当前手机号是否被注册过
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",registerVO.getMobile());
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        //插入用户信息：userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg(UserInfo.USER_AVATAR);
        baseMapper.insert(userInfo);

        //插入用户账户记录：userAccount
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());//关联两表
        userAccountMapper.insert(userAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();
        //判断用户是否存在
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile)
                            .eq("user_type",userType);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        //密码是否正确
        Assert.equals(MD5.encrypt(password),userInfo.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);

        //是否被禁用
        Assert.equals(userInfo.getStatus(),userInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);

        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        //生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        //组装UserInfoVo
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setToken(token);
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setMobile(mobile);
        userInfoVO.setUserType(userType);

        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {
        if (userInfoQuery ==null){
            return baseMapper.selectPage(pageParam,null);
        }

        String mobile = userInfoQuery.getMobile();
        Integer status = userInfoQuery.getStatus();
        Integer userType = userInfoQuery.getUserType();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(mobile),"mobile",mobile)
                    .eq(status != null,"status",status)
                    .eq(userType != null,"user_Type",userType);

        return baseMapper.selectPage(pageParam, queryWrapper);

    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public Boolean checkMobile(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        return count > 0;
    }

    @Override
    public UserIndexVO getIndexUserInfo(Long userId) {
        //用户信息
        UserInfo userInfo = baseMapper.selectById(userId);

        //账户信息
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id",userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);

        //登录日志
        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper.eq("user_id",userId).orderByDesc("id").last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(userLoginRecordQueryWrapper);

        //组装结果对象
        UserIndexVO userIndexVO = new UserIndexVO();
        userIndexVO.setUserId(userId);
        userIndexVO.setUserType(userInfo.getUserType());
        userIndexVO.setName(userInfo.getName());
        userIndexVO.setNickName(userInfo.getNickName());
        userIndexVO.setHeadImg(userInfo.getHeadImg());
        userIndexVO.setBindStatus(userInfo.getBindStatus());
        userIndexVO.setAmount(userAccount.getAmount());
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());

        return userIndexVO;

    }

    @Override
    public String getMobileByBindCode(String bindCode) {

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code",bindCode);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        return userInfo.getMobile();
    }
}
