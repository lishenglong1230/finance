package com.example.finance.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/25 16:05
 */
@Data
@ApiModel(description = "短信")
public class SmsDTO {

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "消息内容")
    private String message;
}
