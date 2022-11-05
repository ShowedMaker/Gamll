package com.atguigu.es;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.es.pojo.Article;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.DynamicTemplate;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName Test2
 * @Description
 * @Author yzchao
 * @Date 2022/11/1 20:37
 * @Version V1.0
 */
public class Test2 {
    public static void main(String[] args) throws UnknownHostException {

        //客户端连接对象初始化：使用一个节点的es 不使用集群
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置需要连接的es的ip和端口 TCP:9300 HTTP:9200
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));

        //声名批量对象
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        for (int i = 1; i <= 1000; i++) {
            Article article = new Article();
            article.setId(i);
            article.setTitle("Elasticsearch 是位于 Elastic Stack 核心的分布式搜索和分析引擎" + i);
            article.setContent("Elasticsearch 为所有类型的数据提供近乎实时的搜索和分析。" +
                    "无论您拥有结构化或非结构化文本、数字数据还是地理空间数据，Elasticsearch " +
                    "都能以支持快速搜索的方式高效地存储和索引它。您可以超越简单的数据检索和聚合信息来发现数据中的趋势和模式。" +
                    "随着您的数据和查询量的增长，Elasticsearch 的分布式特性使您的部署能够随之无缝增长。" + i);

            bulkRequestBuilder.add( client.prepareIndex("java0509_new","article","" + i)
                            .setSource(JSONObject.toJSONString(article), XContentType.JSON));

            if (i % 200 == 0) {
                //新增提交数据
                bulkRequestBuilder.execute().actionGet();
            }
        }
        //关闭连接
        client.close();


    }
}
