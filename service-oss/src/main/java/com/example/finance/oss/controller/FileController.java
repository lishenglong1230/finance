package com.example.finance.oss.controller;

import com.example.common.exception.BusinessException;
import com.example.common.result.R;
import com.example.common.result.ResponseEnum;
import com.example.finance.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/5 17:12
 */

@Api(tags = "阿里云文件管理")
@CrossOrigin //跨域
@RestController
@RequestMapping("/api/oss/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("upload")
    public R upload( @ApiParam(value = "文件", required = true)
                     @RequestParam("file") MultipartFile file,

                     @ApiParam(value = "模块", required = true)
                     @RequestParam("module") String module ){

        String url = null;
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            url = fileService.upload(inputStream, module, originalFilename);
            return R.ok().message("文件上传成功").data("url",url);

        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }



    }

    @ApiOperation("删除oss文件")
    @DeleteMapping("/remove")
    public R remove(
            @ApiParam(value = "要删除的文件",required = true)
            @RequestParam("url") String url){
        fileService.removeFile(url);
        return R.ok().message("删除成功");
    }
}
