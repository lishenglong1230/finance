package com.example.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/1 10:00
 */
@Data
public class R {
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    /**
     * 构造函数私有化
     */
    private R(){

    }

    /**
     * 返回成功结果
     * @return
     */
    public static R ok(){
        R r = new R();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMessage());
        return r;
    }

    public static R error(){
        R r = new R();
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(ResponseEnum.ERROR.getMessage());
        return r;

    }

    /**
     * 设定特定的结果
     * @param responseEnum
     * @return
     */
    public static R setResult(ResponseEnum responseEnum){
        R r = new R();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMessage());
        return r;

    }

    /**
     * 设定特定的消息
     * @param message
     * @return
     */
    public R message(String message){
        this.setMessage(message);
        return this;
    }

    /**
     * 设定特定的响应码
     * @param code
     * @return
     */
    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    /**
     * 设置特定的数据
     * @param key
     * @param o
     * @return
     */
    public R data(String key , Object o){
        this.data.put(key,o);
        return this;
    }

    public R data(Map map){
        this.setData(map);
        return this;
    }


}
