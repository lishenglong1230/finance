package com.example.finance.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.finance.core.pojo.entity.LendReturn;
import com.example.finance.core.mapper.LendReturnMapper;
import com.example.finance.core.service.LendReturnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Service
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {

    @Override
    public List<LendReturn> selectByLendId(Long lendId) {
        QueryWrapper<LendReturn> queryWrapper = new QueryWrapper();
        queryWrapper.eq("lend_id", lendId);
        List<LendReturn> lendReturnList = baseMapper.selectList(queryWrapper);
        return lendReturnList;
    }
}
