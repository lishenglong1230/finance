package com.example.finance.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.core.pojo.entity.BorrowInfo;
import com.example.finance.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.vo.BorrowInfoApprovalVO;

import java.util.List;
import java.util.Map;

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

    //IPage<Lend> listPage(Page<Lend> lendPage);

    List<Lend> selectList();

    Map<String,Object> getLendDetail(Long id);
}
