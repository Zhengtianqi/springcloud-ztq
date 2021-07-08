package com.ztq.utils;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Kafka消息生产者
 *
 * @author: zhengtianqi
 */
public class KafkaProducerUtil {
    private static Producer<String, String> buildProducer;
    private static volatile KafkaProducerUtil instance;
    private static String LOCALIP = "";
    private static Producer<String, String> producer;

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerUtil.class);

    static {
        LOCALIP = "localip";

        // 登录
        Properties props = props();
        SecurityPrepare.kerbrosLogin(props.getProperty("sslDir"), props.getProperty("userKeytab"), props.getProperty("userPrincipal"));
        // 创建生产者实例
        buildProducer = new KafkaProducer<>(props);
    }

    private static Properties props() {
        Properties props = NacosUtils.getConfig("Constants.KAFKA_GROUP", "Constants.KAFKA_DATAID");
        props.setProperty("client.id",
                props.getProperty("client.id") + LOCALIP);
        return props;
    }

    public void send(Object pack, String topic, String key, boolean isNeedCallBack) throws Exception {
        producer = buildProducer;
        // 创建发送实体 ProducerRecord，不指定分区。务必指定有效key，使用fastjson序列化为字符串后发送
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, JSON.toJSONString(pack));

        // 同步发送，Future.get()方法会阻塞直到服务器返回响应
        if (!isNeedCallBack) {
            RecordMetadata metadata = producer.send(record).get();
            logger.info("kafka发送消息成功sync result={}", metadata.toString());
        } else {
            // 异步发送，Future.get()方法会阻塞直到服务器返回响应
            producer.send(record, new ProducerCallback());
        }


    }

    public void close() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("kafka生产者开始关闭");
            producer.close();
        }));
    }


    private static class ProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata metadata, Exception e) {
            if (e == null) {
                // 发送成功，记录日志
                logger.info("kafka生产者发送成功={}", metadata.toString());
            } else {
                // 异常处理
                logger.error("kafka生产者发送失败={}", e);
            }
        }
    }

    private KafkaProducerUtil() {
    }

    public static KafkaProducerUtil getInstance() {
        if (instance == null) {
            synchronized (KafkaProducerUtil.class) {
                if (instance == null) {
                    instance = new KafkaProducerUtil();
                }
            }
        }
        return instance;
    }
}
