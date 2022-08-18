package com.example.finance.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.exception.Assert;
import com.example.common.result.ResponseEnum;
import com.example.finance.core.enums.BorrowAuthEnum;
import com.example.finance.core.enums.BorrowInfoStatusEnum;
import com.example.finance.core.enums.BorrowerStatusEnum;
import com.example.finance.core.enums.UserBindEnum;
import com.example.finance.core.mapper.BorrowerMapper;
import com.example.finance.core.mapper.IntegralGradeMapper;
import com.example.finance.core.mapper.UserInfoMapper;
import com.example.finance.core.pojo.entity.BorrowInfo;
import com.example.finance.core.mapper.BorrowInfoMapper;
import com.example.finance.core.pojo.entity.Borrower;
import com.example.finance.core.pojo.entity.IntegralGrade;
import com.example.finance.core.pojo.entity.UserInfo;
import com.example.finance.core.pojo.vo.BorrowInfoApprovalVO;
import com.example.finance.core.pojo.vo.BorrowerDetailVO;
import com.example.finance.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.core.service.BorrowerService;
import com.example.finance.core.service.DictService;
import com.example.finance.core.service.LendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.awt.geom.QuadCurve2D;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private IntegralGradeMapper integralGradeMapper;
    @Resource
    private DictService dictService;
    @Resource
    private BorrowerMapper borrowerMapper;
    @Resource
    private BorrowerService borrowerService;
    @Resource
    private LendService lendService;


    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        //获取用户积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        //
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.le("integral_start", integral)
                                 .ge("integral_end", integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if (integralGrade == null){
            return new BigDecimal(0);
        }

        return integralGrade.getBorrowAmount();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        //获取userInfo信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //判断用户绑定状态
        Assert.isTrue(userInfo.getBindStatus().intValue() == UserBindEnum.BIND_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_BIND_ERROR);

        //判断借款人额度申请状态
        Assert.isTrue(userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        //判断借款额度是否充足
        BigDecimal borrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(borrowInfo.getAmount().doubleValue() <= borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);

        //存储borrowInfo数据
        borrowInfo.setUserId(userId);//绑定userId
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100))); //将百分数转换为小数
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());//设置借款申请审核状态
        baseMapper.insert(borrowInfo);

    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id",userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);
        if (objects.size()==0){
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }

        Integer status = (Integer)objects.get(0);
        return status;

    }

    @Override
    public List<BorrowInfo> selectList(Long offset, Long limit) {

        List<BorrowInfo> borrowInfoList = baseMapper.selectBorrowInfoList(offset,limit);
        borrowInfoList.forEach(borrowInfo -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());

            borrowInfo.getParam().put("returnMethod",returnMethod);
            borrowInfo.getParam().put("moneyUse",moneyUse);
            borrowInfo.getParam().put("status",status);

        });
        return borrowInfoList;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {

        //查询借款对象：BorrowInfo
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod",returnMethod);
        borrowInfo.getParam().put("moneyUse",moneyUse);
        borrowInfo.getParam().put("status",status);

        //查询借款人对象：Borrower(BorrowerDetailVO)
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id",borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //组装稽核结果
        Map<String, Object> result = new HashMap<>();
        result.put("borrowInfo",borrowInfo);
        result.put("borrowerDetailVO",borrowerDetailVO);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {

        //修改借款审核的状态 borrow_info
        Long borrowInfoId = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);

        //产生新的标的记录 lend
        if (borrowInfoApprovalVO.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()){
            //创建新标的
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);
        }


    }
}
