package com.example.finance.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.R;
import com.example.finance.core.pojo.entity.BorrowInfo;
import com.example.finance.core.pojo.entity.Borrower;
import com.example.finance.core.pojo.vo.BorrowInfoApprovalVO;
import com.example.finance.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/17 16:07
 */
@Api(tags = "借款管理")
@RestController
@RequestMapping("/admin/core/borrowInfo")
@Slf4j
public class AdminBorrowInfoController {
    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("借款信息列表")
    @GetMapping("/list/{page}/{limit}")
    public R list(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit){
        List<BorrowInfo> borrowInfoList=borrowInfoService.selectList(page,limit);
        return R.ok().data("borrowInfoList",borrowInfoList);
    }

    @ApiOperation("借款信息详情")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款信息id",required = true)
            @PathVariable Long id){

        Map<String,Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return R.ok().data("borrowInfoDetail",borrowInfoDetail);
    }

    @ApiOperation("审批借款信息")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO) {

        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.ok().message("审批完成");

    }
}
