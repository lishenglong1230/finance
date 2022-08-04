package com.example.finance.core.mapper;

import com.example.finance.core.pojo.dto.ExcelDictDTO;
import com.example.finance.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<ExcelDictDTO> list);
}
