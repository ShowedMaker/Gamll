package com.atguigu.es;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.es.pojo.Article;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName Test3
 * @Description get就是拿一个 search就是拿多个
 * @Author yzchao
 * @Date 2022/11/2 14:51
 * @Version V1.0
 */
public class Test3 {
    
    /**
     * @Description 文档下标查询: 数据库的主键查询 查询几次?只查询一次文档域
     * @Date 15:10 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main1(String[] args) throws Exception {
        //客户端连接对象初始化：使用一个节点的es 不使用集群
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置需要连接的es的ip和端口 TCP:9300 HTTP:9200
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //文档下标查询: 数据库的主键查询 查询几次?只查询一次文档域  prepareGet()
        GetResponse getResponse = client.prepareGet("java0509_new","article","11").get();
        //获取数据
        System.out.println(getResponse.getSourceAsString());
        //释放连接
        client.close();
    }


    /**
     * @Description 查询所有数据: es默认分页每页10条 查询几次?查询一次
     * @Date 15:17 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main2(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")//.setTypes("article")可以省略 可以用数据库直接查 es弱化类型的概念
                .setQuery(QueryBuilders.matchAllQuery())
                .get();  //查询全部
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 字符串查询 查询几次?查询2次 对于输入的条件有没有分词?分词了 问题:条件只支持字符串
     * @Date 15:41 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main3(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.queryStringQuery("Elasticsearch")
                .field("content"))
                .get();//字符串查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 匹配查询 查询几次?2次 对于输入的条件分词了没?分词了 和字符串基本一样 参数比字符串查询内容丰富
     * @Date 15:52 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main4(String[] args) throws UnknownHostException {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.matchQuery("content","我的搜索"))
                .get();//匹配查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 词条查询 查询几次?2次 对于输入的条件分词了没?不分词
     * @Date 15:57 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.termQuery("content","数据"))
                .get();  //词条查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();

        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 模糊查询 查询2次 对输入的条件不分词
     * @Date 16:24 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main6(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.wildcardQuery("content","*搜索*"))
                .get();  //模糊查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }

    /**
     * @Description 相似度查询 查询2次 对于输入的条件不分词
     * @Date 18:10 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main7(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.fuzzyQuery("content","Elasticsrarch"))
                .get();  //相似度查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }

    /**
     * @Description 范围查询
     * @Date 18:25 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main8(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.rangeQuery("id")
                        .lte(40)
                        .gte(30))
                .get();  //范围查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 组合查询: and or not--->must should must not
     * @Date 18:28 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main9(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("id")
                .lte(500))
                .should(QueryBuilders.fuzzyQuery("content","搜索"))
                .mustNot(QueryBuilders.rangeQuery("id")
                .gte(500)))
                .get();  //组合查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while(iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 分页查询
     * @Date 18:30 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main10(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery())
                .setSize(10)
                .setFrom(0)
                .get();  //分页查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 排序
     * @Date 18:35 2022/11/2
     * @Param [args]
     * @return void
     */
    public static void main11(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery())
                .setSize(10)
                .setFrom(0)
                .addSort("id", SortOrder.DESC)
                .get();  //排序查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            System.out.println(searchHit.getSourceAsString());
        }
        //释放连接
        client.close();
    }


    /**
     * @Description 高亮查询
     * @Date 0:37 2022/11/3
     * @Param [args]
     * @return void
     */
    public static void main12(String[] args) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));


       //定义高亮的域  高亮的域一定要作为查询条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("content");
        highlightBuilder.preTags("<em style=color:blue>");
        highlightBuilder.postTags("</em>");

        //查询所有数据: es默认分页每页10条 查询几次?查询一次
        SearchResponse  searchResponse= client.prepareSearch("java0509_new")
                .setTypes("article")
                .setQuery(QueryBuilders.matchQuery("content","搜索"))
                .setSize(10)
                .setFrom(0)
                .addSort("id", SortOrder.DESC)
                .highlighter(highlightBuilder) //设置高亮
                .get();  //排序查询
        //获取所有命中的数据
        SearchHits hits = searchResponse.getHits();
        System.out.println("一共命中了：" + hits.getTotalHits() + "个");
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //迭代每条数据
        while (iterator.hasNext()) {
            //获取每条数据
            SearchHit searchHit = iterator.next();
            //原始数据取出来
            String sourceAsString = searchHit.getSourceAsString();
            //反序列化  把json反序列化为一个对象
            Article article = JSONObject.parseObject(sourceAsString, Article.class);
            //将每条数据高亮的内容取出来
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");

            if (highlightField != null) {
                //所有高亮的数组
                Text[] fragments = highlightField.getFragments();
                if (fragments != null && fragments.length > 0) {
                    StringBuilder content = new StringBuilder();
                    for (Text fragment : fragments) {
                        content.append(fragment);
                    }


                    //获取高亮的全部数据
                    article.setContent(String.valueOf(content));
                    System.out.println("article = " + article);
                }
            }
        }
        //释放连接
        client.close();
    }




}
