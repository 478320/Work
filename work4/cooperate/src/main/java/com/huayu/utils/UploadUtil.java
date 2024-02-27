package com.huayu.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 上传工具类
 */
public class UploadUtil {
    //阿里域名
    public static final String ALI_DOMAIN = "https://huayu-cooperate.oss-cn-guangzhou.aliyuncs.com/";

    public static String uploadImage(MultipartFile file) throws IOException {
        //生成文件名
        String originalFilename = file.getOriginalFilename();
        String ext = "." + FilenameUtils.getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + ext;
        //地域节点
        String endpoint = "http://oss-cn-guangzhou.aliyuncs.com";
        String accessKeyId = "LTAI5tPajwbJCzdQJPu36TCy";
        String accessKeySecret = "eehqmQFtsjjcjtv9Cc9L9otvhpEQxb";
        //OSS客户端对象
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(
                "huayu-cooperate",
                fileName,
                file.getInputStream()
        );
        ossClient.shutdown();
        return ALI_DOMAIN + fileName;
    }
}
