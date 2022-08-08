package com.example.finance.oss.service;

import java.io.InputStream;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/5 15:12
 */
public interface FileService {
    /**
     * 文件上传至阿里云
     */
    String upload(InputStream inputStream, String module, String fileName);

    /**
     * 删除文件
     * @param url
     */
    void removeFile(String url);
}
