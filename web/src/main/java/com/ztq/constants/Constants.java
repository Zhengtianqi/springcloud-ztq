package com.ztq.constants;

/**
 * @author zhengtianqi
 */
public interface Constants {
    /**
     * 本项目应用名称
     */
    String APP_NAME = "springboot-ztq";
    /**
     * 配置中心url
     */
    String URL_NACOS = "nacos.com";

    /**
     * 配置中心Kafka生产者
     */
    String KAFKA_GROUP = "GROUP_WEB_NACOS";
    String KAFKA_DATAID = "supervision.web-platform.kafka.producer";

    String ES_HTTP_PORT = "127.0.0.1";

    String COMMA_SPLIT = ",";
    /**
     * es索引名称
     */
    String INDEX_PERSON = "my";
}
