package com.example.finance.core.controller.api;

import com.example.common.result.R;
import com.example.finance.core.pojo.entity.Dict;
import com.example.finance.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/15 16:49
 */

@Api(tags = "数据字典")
@RestController
@RequestMapping("/api/core/dict")
@Slf4j
public class DictController {
    @Resource
    private DictService dictService;

    @GetMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(
            @ApiParam(value = "节点编码",required = true)
            @PathVariable String dictCode){

        List<Dict> list = dictService.findByDictCode(dictCode);
        return R.ok().data("dictList",list);


    }
}
