package com.example.finance.core.service;

import com.example.finance.core.pojo.entity.BorrowInfo;
import com.example.finance.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.vo.BorrowInfoApprovalVO;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);
}
