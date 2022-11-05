package com.atguigu.es;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName Test
 * @Description 创建索引 + 映射
 * @Author yzchao
 * @Date 2022/11/1 20:14
 * @Version V1.0
 */
public class Test1 {
    public static void main1(String[] args) throws Exception {
    //客户端连接对象初始化：使用一个节点的es 不使用集群
    TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
    //设置需要连接的es的ip和端口 TCP:9300 HTTP:9200
    client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
    //创建索引-->只创建数据库 create database;-->SQL    json{create:java0509} -->DSL DSL是elasticsearch提供的JSON风格的请求语句，用来操作elasticsearch，实现CRUD
    client.admin().indices().prepareCreate("java0509_new").get();
    //构建映射
    XContentBuilder builder = XContentFactory.jsonBuilder();
    //ik分词器有两套分词模式 ik_smart(最小分词) ik_max_word(最细分词)
        builder
                .startObject()
                .startObject("article")
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .endObject()
                .startObject("title")
                .field("analyzer", "ik_max_word")
                .field("type", "text")
                .endObject()
                .startObject("content")
                .field("analyzer", "ik_max_word")
                .field("type", "text")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
    //构建一个mapping请求对象
        PutMappingRequest request = new PutMappingRequest("java0509_new")
                .type("article").source(builder);
    //存储映射
    client.admin().indices().putMapping(request);
    //关闭连接
        client.close();


    }

    public static void main(String[] args) throws UnknownHostException {
        //客户端连接对象初始化：使用一个节点的es 不使用集群
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        //设置需要连接的es的ip和端口 TCP:9300 HTTP:9200
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"),9300));
        //创建索引-->只创建数据库 create database;-->SQL    json{create:java0509} -->DSL DSL是elasticsearch提供的JSON风格的请求语句，用来操作elasticsearch，实现CRUD
        client.admin().indices().prepareCreate("java0509_new").get();
        //关闭连接
        client.close();
    }
}
