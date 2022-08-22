package com.example.finance.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.R;
import com.example.finance.core.pojo.entity.Lend;
import com.example.finance.core.service.LendService;
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
import java.util.Map;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/18 10:36
 */
@Api(tags = "标的管理")
@RestController
@RequestMapping("/admin/core/lend")
@Slf4j
public class AdminLendController {
    @Resource
    private LendService lendService;

//    @ApiOperation("标的列表")
//    @GetMapping("/list/{page}/{limit}")
//    public R list( @ApiParam(value = "当前页码", required = true)
//                   @PathVariable Long page,
//                   @ApiParam(value = "每页记录数", required = true)
//                   @PathVariable Long limit){
//
//        Page<Lend> lendPage = new Page<>(page, limit);
//        IPage<Lend> lendIPage = lendService.listPage(lendPage);
//        return R.ok().data("pageModel",lendIPage);
//    }

    @ApiOperation("标的列表")
    @GetMapping("/list")
    public R list() {
        List<Lend> lendList = lendService.selectList();
        return R.ok().data("list", lendList);
    }

    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long id) {
        Map<String, Object> result = lendService.getLendDetail(id);
        return R.ok().data("lendDetail", result);
    }

    @ApiOperation("放款")
    @GetMapping("/makeLoan/{id}")
    public R makeLoan(
            @ApiParam(value = "标的id", required = true)
            @PathVariable("id") Long id) {
        lendService.makeLoan(id);
        return R.ok().message("放款成功");
    }
}
