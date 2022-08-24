package com.example.finance.core.service;

import com.example.finance.core.pojo.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.core.pojo.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface LendItemService extends IService<LendItem> {

    String commitInvest(InvestVO investVO);

    void notify(Map<String, Object> paramMap);

    List<LendItem> selectByLendId(Long lendId, Integer status);

    List<LendItem> selectByLendId(Long lendId);
}
