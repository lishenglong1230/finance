package com.example.finance.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.example.common.exception.BusinessException;
import com.example.common.result.R;
import com.example.common.result.ResponseEnum;
import com.example.finance.core.pojo.dto.ExcelDictDTO;
import com.example.finance.core.pojo.entity.Dict;
import com.example.finance.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/3 13:28
 */
@Api(tags = "数据字典管理")
@RestController
@RequestMapping("/admin/core/dict")
@Slf4j
//@CrossOrigin
public class AdminDictController {

    @Resource
    DictService dictService;

    @ApiOperation("Excel数据的批量导入")
    @PostMapping("/import")
    public R batchImport(
            @ApiParam(value = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return R.ok().message("数据字典数据批量导入成功");
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }

    }

    @ApiOperation("Excel数据的导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response){

        try {
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系 (excel文件名称)
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());

        } catch (IOException e) {
            //EXPORT_DATA_ERROR(104, "数据导出失败"),
            throw  new BusinessException(ResponseEnum.EXPORT_DATA_ERROR, e);
        }
    }

    @GetMapping("listByParentId/{parentId}")
    public R listByParentId(
            @ApiParam(value = "上级节点id", required = true)
            @PathVariable Long parentId){
        List<Dict> dictList = dictService.listByParentId(parentId);
        return R.ok().data("list",dictList);

    }


}
