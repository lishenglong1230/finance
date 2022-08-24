package com.example.finance.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.finance.core.pojo.entity.LendItemReturn;
import com.example.finance.core.mapper.LendItemReturnMapper;
import com.example.finance.core.service.LendItemReturnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    @Override
    public List<LendItemReturn> selectByLendId(Long lendId, Long userId) {

        QueryWrapper<LendItemReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("lend_id", lendId)
                .eq("invest_user_id", userId)
                .orderByAsc("current_period");
        return baseMapper.selectList(queryWrapper);
    }
}
