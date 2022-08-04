package com.example.finance.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.core.listener.ExcelDictDTOListener;
import com.example.finance.core.mapper.DictMapper;
import com.example.finance.core.pojo.dto.ExcelDictDTO;
import com.example.finance.core.pojo.entity.Dict;
import com.example.finance.core.service.DictService;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author lishenglong
 * @since 2022-07-29
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    RedisTemplate redisTemplate;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void importData(InputStream inputStream) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("importData finished");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictlist = baseMapper.selectList(null);
        //创建Excel对应的dto，将dict列表转换成ExcelDictDTO列表
        ArrayList<ExcelDictDTO> excelDictDtoList = new ArrayList<>(dictlist.size());
        dictlist.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);//拷贝数据
            excelDictDtoList.add(excelDictDTO);
        });
        return excelDictDtoList;
    }

    /**
     * 根据parentId查询是因为 同级(parentId相等)的需要一起查询出来
     *
     * @param parentId
     * @return
     */
    @Override
    public List<Dict> listByParentId(Long parentId) {

        try {
            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("finance:core:dictList:" + parentId);
            if (dictList!=null) {
                log.info("从redis中获取数据列表");
                return dictList;
            }
        } catch (Exception e) {
            log.error("redis服务器异常 " + ExceptionUtils.getStackTrace(e));
        }

        //如果不存在则查询数据库
        log.info("从数据库中获取列表");
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_Id", parentId);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        dicts.forEach(dict -> {
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });

        //将数据存入redis
        try {
            redisTemplate.opsForValue().set("finance:core:dictList:"+parentId,dicts,5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis服务器异常" + ExceptionUtils.getStackTrace(e));
        }
        return dicts;
    }

    private boolean hasChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_Id", id);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count.intValue() > 0) {
            return true;
        }
        return false;
    }
}
