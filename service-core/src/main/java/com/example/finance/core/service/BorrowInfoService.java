package com.example.finance.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.entity.Borrower;
import com.example.finance.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getBorrowAmount(Long userId);

    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    Integer getStatusByUserId(Long userId);

    List<BorrowInfo> selectList(Long offset,Long limit);

    Map<String, Object> getBorrowInfoDetail(Long id);

    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
