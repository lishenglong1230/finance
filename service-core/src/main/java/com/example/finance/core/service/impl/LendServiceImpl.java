package com.example.finance.core.service.impl;

import com.example.finance.core.enums.LendStatusEnum;
import com.example.finance.core.pojo.entity.BorrowInfo;
import com.example.finance.core.pojo.entity.Lend;
import com.example.finance.core.mapper.LendMapper;
import com.example.finance.core.pojo.vo.BorrowInfoApprovalVO;
import com.example.finance.core.service.LendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.core.util.LendNoUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());
        lend.setTitle(borrowInfoApprovalVO.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());//期数
        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate().divide(new BigDecimal(100)));
        lend.setReturnMethod(borrowInfo.getReturnMethod());
        lend.setLowestAmount(new BigDecimal(100));//最低投资金额
        lend.setInvestAmount(new BigDecimal(0));//已投金额
        lend.setInvestNum(0);//已投人数
        lend.setPublishDate(LocalDateTime.now());

        //起息日期
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate lendStartDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(),dateTimeFormatter);
        lend.setLendStartDate(lendStartDate);
        //结束日期
        LocalDate lendEndDate = lendStartDate.plusMonths(borrowInfo.getPeriod());
        lend.setLendEndDate(lendEndDate);

        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());//标的描述

        //平台预期收益 = 标的金额 *（年化 / 12 * 期数)
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN);
        BigDecimal multiply = monthRate.multiply(new BigDecimal(lend.getPeriod()));
        BigDecimal expectAmount = lend.getAmount().multiply(multiply);
        lend.setExpectAmount(expectAmount);

        //实际收益 结束之后才能计算
        lend.setRealAmount(new BigDecimal(0));
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());//标的状态
        lend.setCheckTime(LocalDateTime.now());//审核时间
        lend.setCheckAdminId(1L);//审核人

        //写入数据库
        baseMapper.insert(lend);
    }
}
