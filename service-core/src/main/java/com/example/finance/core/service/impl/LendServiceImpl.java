package com.example.finance.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.exception.BusinessException;
import com.example.finance.core.enums.LendStatusEnum;
import com.example.finance.core.enums.ReturnMethodEnum;
import com.example.finance.core.enums.TransTypeEnum;
import com.example.finance.core.hfb.HfbConst;
import com.example.finance.core.hfb.RequestHelper;
import com.example.finance.core.mapper.BorrowerMapper;
import com.example.finance.core.mapper.UserAccountMapper;
import com.example.finance.core.mapper.UserInfoMapper;
import com.example.finance.core.pojo.bo.TransFlowBO;
import com.example.finance.core.pojo.entity.*;
import com.example.finance.core.mapper.LendMapper;
import com.example.finance.core.pojo.vo.BorrowInfoApprovalVO;
import com.example.finance.core.pojo.vo.BorrowerDetailVO;
import com.example.finance.core.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.core.util.*;
import com.sun.el.lang.ELArithmetic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Service
@Slf4j
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Resource
    private DictService dictService;
    @Resource
    private BorrowerMapper borrowerMapper;
    @Resource
    private BorrowerService borrowerService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private LendItemService lendItemService;
    @Resource
    private TransFlowService transFlowService;
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
        LocalDate lendStartDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), dateTimeFormatter);
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

   /* @Override
    public IPage<Lend> listPage(Page<Lend> lendPage) {

        Page<Lend> page = baseMapper.selectPage(lendPage, null);
        List<Lend> lendList = page.getRecords();
        lendList.forEach(lend -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod",lend.getReturnMethod());
            String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
            lend.getParam().put("returnMethod",returnMethod);
            lend.getParam().put("status",status);
        });
        return page;

    }*/

    @Override
    public List<Lend> selectList() {
        List<Lend> lendList = baseMapper.selectList(null);
        lendList.forEach(lend -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
            String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
            lend.getParam().put("returnMethod", returnMethod);
            lend.getParam().put("status", status);
        });
        return lendList;
    }

    @Override
    public Map<String, Object> getLendDetail(Long id) {
        //查询 lend
        Lend lend = baseMapper.selectById(id);
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        lend.getParam().put("returnMethod", returnMethod);
        lend.getParam().put("status", status);

        //查询借款人对象：Borrower(BorrowerDetailVO)
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", lend.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //组装集合结果
        Map<String, Object> result = new HashMap<>();
        result.put("lend", lend);
        result.put("borrower", borrowerDetailVO);
        return result;
    }

    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod) {

        BigDecimal interestCount;
        if (returnMethod.intValue() == ReturnMethodEnum.ONE.getMethod()) {
            interestCount = Amount1Helper.getInterestCount(invest, yearRate, totalmonth);

        } else if (returnMethod.intValue() == ReturnMethodEnum.TWO.getMethod()) {
            interestCount = Amount2Helper.getInterestCount(invest, yearRate, totalmonth);

        } else if (returnMethod.intValue() == ReturnMethodEnum.THREE.getMethod()) {
            interestCount = Amount3Helper.getInterestCount(invest, yearRate, totalmonth);

        } else{
            interestCount = Amount4Helper.getInterestCount(invest, yearRate, totalmonth);

        }
        return interestCount;
    }

    @Override
    public void makeLoan(Long id) {
        //获取标的信息
        Lend lend = baseMapper.selectById(id);

        //调用汇付宝放宽接口
        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentProjectCode", lend.getLendNo());//标的编号
        String agentBillNo = LendNoUtils.getLoanNo();//放款编号
        paramMap.put("agentBillNo", agentBillNo);

        //平台收益，放款扣除，借款人借款实际金额=借款金额-平台收益
        //月年化
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN);
        //平台实际收益 = 已投金额 * 月年化 * 标的期数
        BigDecimal realAmount = lend.getInvestAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));

        paramMap.put("mchFee", realAmount); //商户手续费(平台实际收益)
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        log.info("放款参数：" + JSONObject.toJSONString(paramMap));
        //发送同步远程调用
        JSONObject result = RequestHelper.sendRequest(paramMap, HfbConst.MAKE_LOAD_URL);
        log.info("放款结果：" + result.toJSONString());

        //放款失败
        if (!"0000".equals(result.getString("resultCode"))) {
            throw new BusinessException(result.getString("resultMsg"));
        }

        //放款成功
        //(1)标的状态和标的平台收益：更新标的相关信息
        lend.setRealAmount(realAmount);//平台收益
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);

        //(2) 给借款账号转入金额
        //获取借款人bindCode
        Long userId = lend.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        //转账
        BigDecimal voteAmt = new BigDecimal(result.getString("voteAmt"));
        userAccountMapper.updateAccount(bindCode,voteAmt,new BigDecimal(0));

        //(3)增加借款交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                result.getString("agentBillNo"),
                bindCode,
                voteAmt,
                TransTypeEnum.BORROW_BACK,
                "项目放款，项目编号： "+lend.getLendNo() + "，项目名称："+lend.getTitle()
        );
        transFlowService.saveTransFlow(transFlowBO);

        //(4)接触并扣除投资人资金
        //获取标的下的投资列表
        List<LendItem> lendItemsList = lendItemService.selectByLendId(id, 1);
        lendItemsList.forEach(item -> {
            Long investUserId = item.getInvestUserId();
            UserInfo investUserInfo = userInfoMapper.selectById(investUserId);
            String investBindCode = investUserInfo.getBindCode();
            BigDecimal investAmount = item.getInvestAmount();
            userAccountMapper.updateAccount(investBindCode,new BigDecimal(0),investAmount.negate());
            //(5)
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    LendNoUtils.getTransNo(),
                    investBindCode,
                    investAmount,
                    TransTypeEnum.INVEST_UNLOCK,
                    "项目放款，冻结资金转出，项目编号：" + lend.getLendNo() + "，项目名称" + lend.getTitle()
                    );
            transFlowService.saveTransFlow(investTransFlowBO);
        });





    }
}
