package com.example.finance.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.vo.BorrowerApprovalVO;
import com.example.finance.core.pojo.vo.BorrowerDetailVO;
import com.example.finance.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);

    Integer getStatusByUserId(Long userId);

    IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword);

    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    void approval(BorrowerApprovalVO borrowerApprovalVO);
}
