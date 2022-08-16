package com.example.finance.core.service;

import com.example.finance.core.pojo.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {
        List<BorrowerAttachVO> selectBorrowerAttachVOList(Long borrowerId);
}
