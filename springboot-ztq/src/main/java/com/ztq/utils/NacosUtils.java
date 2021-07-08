package com.ztq.utils;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 配置中心工具类
 *
 * @author zhengtianqi
 */
public class NacosUtils {
    private static ConfigService configService;

    /**
     * 读取配置超时时间，单位 ms
     */
    private static final int TIMEOUT = 1000 * 3;
    /**
     * 获取配置文件内容
     */
    private static String content = "";
    private final static Logger logger = LoggerFactory.getLogger(NacosUtils.class);

    static {
        try {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, "Constants.URL_NACOS");
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            logger.error("连接配置中心失败!", e);
            System.exit(1);
        }
    }

    /**
     * 获取配置中心配置内容
     *
     * @param group  命名空间
     * @param dataId 数据库
     * @return Properties
     */
    public static Properties getConfig(String group, String dataId) {
        Properties properties = null;
        try {
            String config = configService.getConfig(dataId, group, 3000);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
            properties = new Properties();
            properties.load(byteArrayInputStream);
        } catch (Exception e) {
            logger.error("", "从配置中心获取配置失败，group={},dataId={}", group, dataId, e);
        }
        if (null == properties) {
            logger.info("", "从配置中心获取配置失败，group={},dataId={}", group, dataId);
        }
        return properties;
    }

    /**
     * 动态读取nocas配置内容
     *
     * @param dataId 配置ID
     * @param group  分组
     * @return
     */
    public static Properties getConfigProperties(String dataId, String group) {
        Properties properties = null;
        try {
            content = configService.getConfig(dataId, group, TIMEOUT);
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    content = configInfo;
                    logger.info("修改后的配置ID是：[" + dataId + "]，配置分组是：[" + group + "]获取的配置信息是" + content);
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            properties = new Properties();
            properties.load(byteArrayInputStream);
        } catch (NacosException e) {
            logger.error("Nacos读取配置超时或网络异常", e);
        } catch (IOException e) {
            logger.error("加载到properties对象出现IO异常", e);
        }
        return properties;
    }


}
