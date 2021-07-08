package com.ztq.utils;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.Properties;

/**
 * @author zhengtianqi
 * @date 2021/7/7
 */
public class EsRestHighLevelClient {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerUtil.class);


    private EsRestHighLevelClient() {
    }

    private static class InternalClass {
        private static RestHighLevelClient client;

        static {
            try {
                Properties properties = NacosUtils.getConfig("Constants.GROUP_WEB_NACOS", "Constants.ES_DATAID");
                String ip = properties.getProperty("es.server.ip");
                String[] ips = ip.split(",");

                HttpHost[] httpHosts = new HttpHost[ips.length];
                for (int i = 0; i < ips.length; i++) {
                    httpHosts[i] = new HttpHost(ips[i], 9200, "https");
                }

                SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
                RestClientBuilder restClientBuilder = RestClient.builder(httpHosts).setFailureListener(sniffOnFailureListener).setHttpClientConfigCallback(httpClientBuilder -> {
                    //最大连接数
                    httpClientBuilder.setMaxConnTotal(100);
                    httpClientBuilder.setMaxConnPerRoute(50);
                    //身份认证
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(properties.getProperty("es.user"), properties.getProperty("es.password")));
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

                    // 启用https
                    SSLContext sc = null;
                    try {
                        sc = SSLContext.getInstance("SSL");
                        sc.init(null, trustAllCerts, new SecureRandom());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SSLIOSessionStrategy sessionStrategy = new SSLIOSessionStrategy(sc, new NullHostNameVerifier());
                    httpClientBuilder.setSSLStrategy(sessionStrategy);

                    return httpClientBuilder;
                }).setRequestConfigCallback(requestConfigBuilder -> {
                    // 超时设置
                    requestConfigBuilder.setConnectTimeout(2000).setConnectionRequestTimeout(2000);
                    return requestConfigBuilder;
                });

                client = Optional.of(restClientBuilder).map(RestHighLevelClient::new).orElse(null);

                //自动嗅探
//                Sniffer sniffer = Sniffer.builder(client.getLowLevelClient()).setSniffAfterFailureDelayMillis(30000).build();
//                sniffOnFailureListener.setSniffer(sniffer);
            } catch (Exception e) {
                logger.error("初始化RestHighLevelClient时出错!", e);
            }
            if (null == client) {
                logger.error("创建ES连接失败!");
            }
        }
    }

    /**
     * 返回单例的Client(ES)
     */
    public static RestHighLevelClient getEsClient() {
        return InternalClass.client;
    }


    private static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    public static class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }

    public static void main(String[] args) throws IOException {
        RestHighLevelClient esClient = EsRestHighLevelClient.getEsClient();

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("字段", "值"));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(query).size(10);
        SearchRequest searchRequest = new SearchRequest("索引").source(searchSourceBuilder);
        SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search);
        esClient = getEsClient();
        search = esClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search);


    }
}
