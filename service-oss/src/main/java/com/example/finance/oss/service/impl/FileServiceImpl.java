package com.example.finance.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.example.finance.oss.service.FileService;
import com.example.finance.oss.util.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/5 15:13
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * @param inputStream
     * @param module      路径
     * @param fileName    文件名称
     * @return
     */
    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        String bucketName = "examplebucket";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET);
        //判断BUCKET_NAME是否存在
        if (!ossClient.doesBucketExist(OssProperties.BUCKET_NAME)) {
            ossClient.createBucket(OssProperties.BUCKET_NAME);
            ossClient.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
        }
        // 上传文件流
        // 文件目录结构 "avatar/2022/08/05/uuid." 文件过多如何分类 uuid解决名称乱码
        //构建日期
        String timeFolder = new DateTime().toString("/yyyy/MM/dd/");
        //文件名生成
        fileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("." ));

        String key = module + timeFolder + fileName;
        ossClient.putObject(OssProperties.BUCKET_NAME, key, inputStream);
        ossClient.shutdown();

        //https://finance-file-bigdragon.oss-cn-beijing.aliyuncs.com/avatar/%E5%8C%97%E4%BA%AC%E5%9C%B0%E9%93%81.gif
        //阿里云文件绝对路径
        return "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/" + key;
    }

    @Override
    public void removeFile(String url) {

        //https://finance-file-bigdragon.oss-cn-beijing.aliyuncs.com/
        //test/2022/08/05/b331727a-8afc-4326-ad31-b71d32be46fd.png
        String host = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        String objectName = url.substring(host.length()); //从参数后面往后截

        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT,
                OssProperties.KEY_ID,
                OssProperties.KEY_SECRET);

        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(OssProperties.BUCKET_NAME, objectName);//如果只有一个文件 连同文件夹一起删除

        ossClient.shutdown();
    }
}