package com.example.finance.core.mapper;

import com.example.finance.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    //List<BorrowInfo> selectBorrowInfoList(@Param("offset") Long offset,@Param("limit") Long limit);

    List<BorrowInfo> selectBorrowInfoList();
}
