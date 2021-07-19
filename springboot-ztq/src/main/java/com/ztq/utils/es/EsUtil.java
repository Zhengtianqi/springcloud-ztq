package com.ztq.utils.es;

import com.alibaba.fastjson.JSON;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhengtianqi
 * @date 2021/7/7
 * es version   7.13.3
 */
public class EsUtil {
    /**
     * 默认类型(整个type类型传闻在8.0版本后可能会废弃,但是目前7.13版本下先保留)
     */
    public static final String DEFAULT_TYPE = "_doc";

    /**
     * set方法前缀
     */
    public static final String SET_METHOD_PREFIX = "set";

    /**
     * 返回状态-CREATED
     */
    public static final String RESPONSE_STATUS_CREATED = "CREATED";

    /**
     * 返回状态-OK
     */
    public static final String RESPONSE_STATUS_OK = "OK";

    /**
     * 返回状态-NOT_FOUND
     */
    public static final String RESPONSE_STATUS_NOT_FOUND = "NOT_FOUND";

    /**
     * 需要过滤的文档数据
     */
    public static final String[] IGNORE_KEY = {"@timestamp", "@version", "type"};

    /**
     * 超时时间 1s
     */
    public static final TimeValue TIME_VALUE_SECONDS = TimeValue.timeValueSeconds(1);

    /**
     * 批量新增
     */
    public static final String PATCH_OP_TYPE_INSERT = "insert";

    /**
     * 批量删除
     */
    public static final String PATCH_OP_TYPE_DELETE = "delete";

    /**
     * 批量更新
     */
    public static final String PATCH_OP_TYPE_UPDATE = "update";

//==========================================数据操作(工具)(不参与调用es)=================================================

    /**
     * 方法描述: 剔除指定文档数据,减少不必要的循环
     *
     * @param map 文档数据
     * @return: void
     * @author: zhengtianqi 2021/7/12
     */
    private static void ignoreSource(Map<String, Object> map) {
        for (String key : IGNORE_KEY) {
            map.remove(key);
        }
    }


    /**
     * 方法描述: 将文档数据转化为指定对象
     *
     * @param sourceAsMap 文档数据
     * @param clazz       转换目标Class对象
     * @return 对象
     * @author: zhengtianqi 2021/7/12
     */
    private static <T> T dealObject(Map<String, Object> sourceAsMap, Class<T> clazz) {
        try {
            ignoreSource(sourceAsMap);
            Iterator<String> keyIterator = sourceAsMap.keySet().iterator();
            T t = clazz.newInstance();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String replaceKey = key.replaceFirst(key.substring(0, 1), key.substring(0, 1).toUpperCase());
                Method method = null;
                try {
                    method = clazz.getMethod(SET_METHOD_PREFIX + replaceKey, sourceAsMap.get(key).getClass());
                } catch (NoSuchMethodException e) {
                    continue;
                }
                method.invoke(t, sourceAsMap.get(key));
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//==========================================索引操作=================================================

    /**
     * 方法描述: 创建索引,若索引不存在且创建成功,返回true,若同名索引已存在,返回false
     *
     * @param: [index] 索引名称
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean insertIndex(String index) {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(index);
        //执行创建请求IndicesClient,请求后获得响应
        try {
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 方法描述: 判断索引是否存在,若存在返回true,若不存在或出现问题返回false
     *
     * @param: [index] 索引名称
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean isExitsIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 方法描述: 删除索引,删除成功返回true,删除失败返回false
     *
     * @param: [index] 索引名称
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean deleteIndex(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//==========================================文档操作(新增,删除,修改)=================================================

    /**
     * 方法描述: 新增/修改文档信息
     *
     * @param index 索引
     * @param id    文档id
     * @param data  数据
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean insertOrUpdateDocument(String index, String id, Object data) {
        try {
            IndexRequest request = new IndexRequest(index);
            request.timeout(TIME_VALUE_SECONDS);
            if (id != null && id.length() > 0) {
                request.id(id);
            }
            request.source(JSON.toJSONString(data), XContentType.JSON);
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            String status = response.status().toString();
            if (RESPONSE_STATUS_CREATED.equals(status) || RESPONSE_STATUS_OK.equals(status)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 方法描述: 更新文档信息
     *
     * @param index 索引
     * @param id    文档id
     * @param data  数据
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean updateDocument(String index, String id, Object data) {
        try {
            UpdateRequest request = new UpdateRequest(index, id);
            request.doc(JSON.toJSONString(data), XContentType.JSON);
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            String status = response.status().toString();
            if (RESPONSE_STATUS_OK.equals(status)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 方法描述:删除文档信息
     *
     * @param index 索引
     * @param id    文档id
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean deleteDocument(String index, String id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            String status = response.status().toString();
            if (RESPONSE_STATUS_OK.equals(status)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 方法描述: 小数据量批量新增
     *
     * @param index    索引
     * @param dataList 数据集 新增修改需要传递
     * @param timeout  超时时间 单位为秒
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean simplePatchInsert(String index, List<Object> dataList, long timeout) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.timeout(TimeValue.timeValueSeconds(timeout));
            if (dataList != null && dataList.size() > 0) {
                for (Object obj : dataList) {
                    bulkRequest.add(
                            new IndexRequest(index)
                                    .source(JSON.toJSONString(obj), XContentType.JSON)
                    );
                }
                RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
                BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (!response.hasFailures()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//==========================================文档操作(查询)=================================================

    /**
     * 方法描述: 判断文档是否存在
     *
     * @param index 索引
     * @param id    文档id
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean isExistsDocument(String index, String id) {
        return isExistsDocument(index, DEFAULT_TYPE, id);
    }


    /**
     * 方法描述: 判断文档是否存在
     *
     * @param index 索引
     * @param type  类型
     * @param id    文档id
     * @return: boolean
     * @author: zhengtianqi 2021/7/12
     */
    public static boolean isExistsDocument(String index, String type, String id) {
        GetRequest request = new GetRequest(index, type, id);
        try {
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.isExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 方法描述: 根据id查询文档
     *
     * @param index 索引
     * @param id    文档id
     * @param clazz 转换目标Class对象
     * @return 对象
     * @author: zhengtianqi 2021/7/12
     */
    public static <T> T selectDocumentById(String index, String id, Class<T> clazz) {
        return selectDocumentById(index, DEFAULT_TYPE, id, clazz);
    }


    /**
     * 方法描述: 根据id查询文档
     *
     * @param index 索引
     * @param type  类型
     * @param id    文档id
     * @param clazz 转换目标Class对象
     * @return 对象
     * @author: zhengtianqi 2021/7/12
     */
    public static <T> T selectDocumentById(String index, String type, String id, Class<T> clazz) {
        try {
            type = type == null || type.equals("") ? DEFAULT_TYPE : type;
            GetRequest request = new GetRequest(index, type, id);
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                Map<String, Object> sourceAsMap = response.getSourceAsMap();
                return dealObject(sourceAsMap, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 方法描述:（筛选条件）获取数据集合
     *
     * @param index         索引
     * @param sourceBuilder 请求条件
     * @param clazz         转换目标Class对象
     * @return: java.util.List<T>
     * @author: zhengtianqi
     */
    public static <T> List<T> selectDocumentList(String index, SearchSourceBuilder sourceBuilder, Class<T> clazz) {
        try {
            SearchRequest request = new SearchRequest(index);
            if (sourceBuilder != null) {
                // 返回实际命中数
                sourceBuilder.trackTotalHits(true);
                request.source(sourceBuilder);
            }
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if (response.getHits() != null) {
                List<T> list = new ArrayList<>();
                SearchHit[] hits = response.getHits().getHits();
                for (SearchHit documentFields : hits) {
                    Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
                    list.add(dealObject(sourceAsMap, clazz));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 方法描述: 筛选查询,返回使用了<span style='color:red'></span>处理好的数据.
     *
     * @param: index 索引名称
     * @param: sourceBuilder sourceBuilder对象
     * @param: clazz 需要返回的对象类型.class
     * @param: highLight 需要表现的高亮匹配字段
     * @return: java.util.List<T>
     * @author: zhengtianqi 2021/7/12
     */
    public static <T> List<T> selectDocumentListHighLight(String index, SearchSourceBuilder sourceBuilder, Class<T> clazz, String highLight) {
        try {
            SearchRequest request = new SearchRequest(index);
            if (sourceBuilder != null) {
                // 返回实际命中数
                sourceBuilder.trackTotalHits(true);
                //高亮
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field(highLight);
                //多个高亮关闭
                highlightBuilder.requireFieldMatch(false);
                highlightBuilder.preTags("<span style='color:red'>");
                highlightBuilder.postTags("</span>");
                sourceBuilder.highlighter(highlightBuilder);
                request.source(sourceBuilder);
            }
            RestHighLevelClient client = EsRestHighLevelClient.getEsClient();
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if (response.getHits() != null) {
                List<T> list = new ArrayList<>();
                for (SearchHit documentFields : response.getHits().getHits()) {
                    Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
                    HighlightField title = highlightFields.get(highLight);
                    Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
                    if (title != null) {
                        Text[] fragments = title.fragments();
                        String n_title = "";
                        for (Text fragment : fragments) {
                            n_title += fragment;
                        }
                        //高亮替换原来的内容
                        sourceAsMap.put(highLight, n_title);
                    }
                    list.add(dealObject(sourceAsMap, clazz));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        //创建索引
//        boolean a = EsRestHighLevelClient.insertIndex("my");
//        System.out.println(a);

        //索引是否存在
        boolean b = EsUtil.isExitsIndex("my");
        System.out.println(b);


        //删除索引
//        boolean a = EsUtil.deleteIndex("my");
//        System.out.println(a);

        //新增/修改文档
//        Map<String,Object> paramMap = new HashMap<String,Object>();
//        paramMap.put("name","张22222三");
//        paramMap.put("age",16);
//        paramMap.put("sal",12500.00);
//        paramMap.put("addr","杭州市西湖区");
//        boolean a = EsUtil.insertOrUpdateDocument("my","2",paramMap);
//        System.out.println(a);

        //修改文档信息
//        Map<String,Object> paramMap = new HashMap<String,Object>();
//        paramMap.put("name","李四");
//        paramMap.put("age",16);
//        paramMap.put("sal",12500.00);
//        paramMap.put("addr","杭州市西湖区");
//        boolean a = EsUtil.updateDocument("my","1",paramMap);
//        System.out.println(a);

        //

        //删除文档信息
//        boolean a = EsUtil.deleteDocument("my","2");
//        System.out.println(a);


//        简单批量新增
//        List<Object> list = new ArrayList<>();
//        list.add(new testPOJO("张三1",11,10000.11,"西湖区别墅122号"));
//        list.add(new testPOJO("张三2",12,10000.12,"西湖区别墅123号"));
//        list.add(new testPOJO("张三3",13,10000.13,"西湖区别墅124号"));
//        list.add(new testPOJO("张三4",14,10000.14,"西湖区别墅125号"));
//        list.add(new testPOJO("张三5",15,10000.15,"西湖区别墅126号"));
//        list.add(new testPOJO("张三6",16,10000.16,"西湖区别墅127号"));
//        list.add(new testPOJO("张三7",17,10000.17,"西湖区别墅128号"));
//        list.add(new testPOJO("张三8",18,10000.18,"西湖区别墅129号"));
//        boolean b = EsUtil.simplePatchInsert("my",list,10L);
//        System.out.println(b);


        //判断文档是否存在
//        boolean a = EsUtil.isExistsDocument("my","1");
//        System.out.println(a);

        //根据id查询文档
//        testPOJO pojo = EsUtil.selectDocumentById("my","xLP9VnoBngRFmC_Ti0ak",testPOJO.class);
//        System.out.println(pojo);

        //根据查询条件获取数据集
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(QueryBuilders.matchQuery("name","张三1"));
//        List<testPOJO> list = EsUtil.selectDocumentList("my",builder,testPOJO.class);
//        for (testPOJO test : list) {
//            System.out.println(test);
//        }

        //高亮查询
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(QueryBuilders.matchPhraseQuery("name", "张三1")); //使用termQuery查询是一个BUG,无法查询匹配,可以使用matchPhraseQuery替代,该方法表示不使用分词,而是以一个短语的形式查询
//        List<testPOJO> list = EsUtil.selectDocumentListHighLight("my", builder, testPOJO.class, "name");
//        for (testPOJO test : list) {
//            System.out.println(test);
//        }
    }
}
