package com.wuw.common.server.service;


import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.wuw.common.api.apiResult.ApiResult;
import com.wuw.common.server.model.OSSUplodVO;
import com.wuw.common.server.model.STSTicketDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
public class ALiOSS {





    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.file.keyid}")
    private String accessKeyId;

    @Value("${aliyun.oss.file.keysecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.file.bucketname}")
    private String bucketName;

    //////////////////////私有桶
    @Value("${aliyun.oss.sts.endpoint}")
    private String STSEndpoint;

    @Value("${aliyun.oss.sts.keyid}")
    private String STSKeyId;

    @Value("${aliyun.oss.sts.keysecret}")
    private String STSKeySecret;

    @Value("${aliyun.oss.sts.roleArn}")
    private String STSRoleArn;
    @Value("${aliyun.oss.sts.bucketname}")
    private String STSBucketName;


    /**
     * 上传公有桶及获取链接地址
     * @param inputStream
     * @param objectName
     * @return
     */
    public String uploadFileAvatar(InputStream inputStream, String objectName) {

        try{
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            //调用OSS方法实现上传
            //第一个参数 Bucket名称
            //第二个参数  上传到OSS文件路径和文件名称
            //第三个参数  上传文件输入流
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            //把上传之后文件路径返回
            //需要把上传到阿里云oss路径手动拼接出来
            String url = "";
            url = "https://"+bucketName+"."+endpoint+"/"+objectName;

            return url;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    ///////////////////////////////////////////////////////////////////////私有桶


    /**
     * STS获取令牌
     * @param roleSessionName
     * @return
     */
    public ApiResult<STSTicketDTO> getAcs(String roleSessionName) {


        // 1. 先从redis获取
        STSTicketDTO stsTicketDTO = null;

        // STSTicketDTO stsTicketDTO = null;
        if (null != stsTicketDTO){
            return ApiResult.success(stsTicketDTO);
        }

        // 2.
        try {

            IClientProfile profile = DefaultProfile.getProfile("", STSKeyId, STSKeySecret);
            String policy = "{\n" +
                    "    \"Version\": \"1\", \n" +
                    "    \"Statement\": [\n" +
                    "        {\n" +
                    "            \"Action\": [\n" +
                    "                \"oss:*\"\n" +
                    "            ], \n" +
                    "            \"Resource\": [\n" +
                    "                \"acs:oss:*:*:internal01/*\" \n" +
                    "            ], \n" +
                    "            \"Effect\": \"Allow\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 适用于Java SDK 3.12.0及以上版本。
            //request.setSysMethod(MethodType.POST);
            // 适用于Java SDK 3.12.0以下版本。
            request.setMethod(MethodType.POST);
            request.setRoleArn(STSRoleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy); // 如果policy为空，则用户将获得该角色下所有权限。
            request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
            final com.aliyuncs.sts.model.v20150401.AssumeRoleResponse response = client.getAcsResponse(request);
            stsTicketDTO = new STSTicketDTO();
            stsTicketDTO.setSecurityId(response.getCredentials().getAccessKeyId());
            stsTicketDTO.setSecurityKey(response.getCredentials().getAccessKeySecret());
            stsTicketDTO.setSecurityToken(response.getCredentials().getSecurityToken());
            stsTicketDTO.setRequestId(response.getRequestId());

            // 保存redis

        }catch (Exception e){

        }

        return ApiResult.success(stsTicketDTO);

    }


    /**
     * 下载
     * todo 从acs获取的SecurityId、SecurityToken
     * @param inputStream
     * @param ossUplodVO
     * @return
     */
    public ApiResult<Boolean> uploadOfSTS(InputStream inputStream, OSSUplodVO ossUplodVO) {
        boolean result = false;
        try{
            OSS ossClient = new OSSClientBuilder().build(endpoint, ossUplodVO.getSecurityId(), ossUplodVO.getSecurityKey(), ossUplodVO.getSecurityToken());
            PutObjectRequest putObjectRequest = new PutObjectRequest(STSBucketName, ossUplodVO.getOssPath()+"/"+ossUplodVO.getFileName(), inputStream);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            ossClient.shutdown();
            result = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return ApiResult.success(result);
    }


    /**
     * 私有桶下载地址
     * todo 从acs获取的SecurityId、SecurityToken
     * @param id
     * @return
     */
    public ApiResult<String> downOfSTS(String id) {

        STSTicketDTO result = null;
        OSS ossClient = new OSSClientBuilder().build(endpoint, result.getSecurityId(), result.getSecurityKey(), result.getSecurityToken());

        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(STSBucketName, "bojectName-即路径"+"/"+"文件名称", HttpMethod.GET);
        request.setExpiration(expiration);
        // 通过HTTP GET请求生成签名URL。
        URL signedUrl = ossClient .generatePresignedUrl(request);
        String s = signedUrl.toString();
        String cdnUrl = "https://osspri01.6noblexc.com"+ s.substring(s.lastIndexOf("com")+3);
        return ApiResult.success(cdnUrl);

    }


}
