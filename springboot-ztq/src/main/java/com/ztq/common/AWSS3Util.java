package com.ztq.common;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 与云存储服务 digitalocean spaces 通信工具类
 * 使用 AWS S3 的SDK
 */
public class AWSS3Util {

    private static String accessKey = "";
    private static String secretKey = "";
    private static String endpointUrl = "";


    private volatile static AWSS3Util awsS3Util;
    private static AmazonS3 s3;
    private static String region = "default";

    private AWSS3Util() {

    }

    /**
     * 获取带签名认证文件的url
     */
    public URL getPresignedUrl(String bucket, String key) {
        URL url = s3.generatePresignedUrl(bucket, key, getDateAfter(1));
        return url;
    }

    /**
     * 获取文件的url
     */
    public URL getUrl(String bucket, String key) {
        URL url = s3.getUrl(bucket, key);
        return url;
    }


    /**
     * 获取指定bucket内所有文件名
     *
     * @param bucket
     */
    public List<String> getFileNames(String bucket) {
        List<String> fileNames = new ArrayList<>();
        ObjectListing objectListing = s3.listObjects(bucket);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

        for (S3ObjectSummary s3ObjectSummary : objectSummaries) {
            String name = s3ObjectSummary.getKey();
            fileNames.add(name.substring(name.lastIndexOf("/") + 1));
        }
        return fileNames;
    }

    /**
     * 获取指定路径前缀的bucket内所有文件名
     *
     * @param bucket
     */
    public List<String> getFiles(String bucket, String prefix) {
        List<String> files = new ArrayList<>();
        ObjectListing objectListing = s3.listObjects(bucket, prefix);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

        for (S3ObjectSummary s3ObjectSummary : objectSummaries) {
            String name = s3ObjectSummary.getKey();
            files.add(name.substring(name.lastIndexOf("/") + 1));
        }
        return files;
    }

    public void deleteObject(String bucket, String key) {
        s3.deleteObject(bucket, key);
    }

    public static AWSS3Util getInstance() {
        if (awsS3Util == null) {
            synchronized (AWSS3Util.class) {
                if (awsS3Util == null) {
                    awsS3Util = new AWSS3Util();
                    s3 = AmazonS3ClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                            .withEndpointConfiguration(new EndpointConfiguration(endpointUrl, region)).withPathStyleAccessEnabled(true)
                            .build();
                }
            }
        }
        return awsS3Util;
    }

    /**
     * 得到几天后的时间
     *
     * @param day
     * @return
     */
    private Date getDateAfter(int day) {
        Date date = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }
}