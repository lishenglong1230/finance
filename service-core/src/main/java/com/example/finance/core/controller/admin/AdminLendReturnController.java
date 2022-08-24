package com.example.finance.core.controller.admin;

import com.example.common.result.R;
import com.example.finance.core.pojo.entity.LendReturn;
import com.example.finance.core.service.LendReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @Date: 2022/8/24 14:23
 */
@Api(tags = "还款记录")
@RestController
@RequestMapping("/admin/core/lendReturn")
@Slf4j
public class AdminLendReturnController {
    @Resource
    private LendReturnService lendReturnService;

    @ApiOperation("获取列表")
    @GetMapping("/list/{lendId}")
    public R list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId) {
        List<LendReturn> list = lendReturnService.selectByLendId(lendId);
        return R.ok().data("list", list);
    }
}
