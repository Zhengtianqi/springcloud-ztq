package com.ztq.utils.es;

import com.ztq.constants.Constants;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * es连接客户端
 *
 * @author zhengtianqi
 */
public class EsRestHighLevelClient {
    public static Logger log = LoggerFactory.getLogger(EsUtil.class.toString());

    private EsRestHighLevelClient() {
    }

    /**
     * 返回单例的Client(ES)
     */
    public static RestHighLevelClient getEsClient() {
        return InternalClass.client;
    }

    private static class InternalClass {
        private static RestHighLevelClient client;

        static {
            try {
                String ip = Constants.ES_HTTP_PORT;
                String[] ips = ip.split(Constants.COMMA_SPLIT);

                HttpHost[] httpHosts = new HttpHost[ips.length];

                for (int i = 0; i < ips.length; i++) {
                    httpHosts[i] = new HttpHost(ips[i], 9200, "http");
                }
                client = new RestHighLevelClient(RestClient.builder(httpHosts));

            } catch (Exception e) {
                log.error("初始化RestHighLevelClient时出错!");
            }
            if (null == client) {
                log.error("创建ES连接失败!");
            }
        }
    }

    /**
     * 测试类
     *
     * @param args main方法参数
     * @throws IOException 抛出异常 无需处理
     */
    public static void main(String[] args) throws IOException {
        RestHighLevelClient esClient = EsRestHighLevelClient.getEsClient();

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(query).size(10);
        SearchRequest searchRequest = new SearchRequest(Constants.INDEX_PERSON).source(searchSourceBuilder);
        SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search);
        esClient = getEsClient();
        search = esClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search);
    }

}
