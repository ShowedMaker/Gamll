package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName SearchServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/3 14:11
 * @Version V1.0
 */
@Service
@Log4j2
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * @param searchData
     * @return java.util.List<com.atguigu.gmall.model.list.Goods>
     * @Description 首页商品商品输入框搜索
     * @Date 14:11 2022/11/3
     * @Param [search]
     */
    @Override
    public Map<String, Object> searchGoods(Map<String, String> searchData) {
        try {
            //拼接条件
            SearchRequest searchRequest = buildRequestParams(searchData);
            //执行查询  即搜出商品也会聚合
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //返回结果解析
            return getSearchResult(searchResponse);
        } catch (IOException e) {
           log.error("商品搜索发生错误，错误信息位:" +e.getMessage());
        }
        return null;
    }


    //返回结果解析
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        SearchHits searchHits = searchResponse.getHits();

        //一共有多少数据符合
        long totalHits = searchHits.getTotalHits();


        //返回结果初始化
        Map<String, Object> map = new HashMap<>();

        map.put("totalHits",totalHits);

        //商品集合初始化
        List<Goods> goodsList = new ArrayList<>();
        //获取迭代器
        Iterator<SearchHit> iterator = searchHits.iterator();
        //开始迭代
        while(iterator.hasNext()){
            SearchHit next = iterator.next();
            //获取原始数据
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods goods = JSONObject.parseObject(sourceAsString,Goods.class);

            goodsList.add(goods);
        }
        //保存商品列表
        map.put("goodsList",goodsList);

        //全部的聚合结果
        Aggregations aggregations = searchResponse.getAggregations();

        //解析品牌的结果
        List<SearchResponseTmVo> aggTmResult = getAggTmResult(aggregations);
        //保存品牌列表
        map.put("aggTmResult",aggTmResult);
        //解析平台属性的聚合结果
        List<SearchResponseAttrVo> attrInfoAggResult = getAttrInfoAggResult(aggregations);
        //保存平套属性的列表
        map.put("attrInfoAggResult", attrInfoAggResult);
        //返回
        return map;
    }


    //解析平台属性的聚合结果
    private List<SearchResponseAttrVo> getAttrInfoAggResult(Aggregations aggregations) {
        //通过id获取nexted的聚合结果
        ParsedNested aggAttrs = aggregations.get("aggAttrs");

        //获取子聚合的结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        // //遍历获取每个平台属性的id和子聚合的结果
        List<SearchResponseAttrVo> list = aggAttrId.getBuckets().stream().map(bucket -> {

            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();

            //获取平台属性的id
            long attrInfoId = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrInfoId);

            //获取子聚合的结果
            ParsedStringTerms aggAttrName = bucket.getAggregations().get("aggAttrName");
            List<? extends Terms.Bucket> aggAttrNameBuckets = aggAttrName.getBuckets();
            if (aggAttrNameBuckets != null && aggAttrNameBuckets.size() > 0) {
                //获取平台属性的名字
                String attrInfoName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrInfoName);
            }

            //获取平台属性值的聚合结果   所有值
            ParsedStringTerms aggAttrValue = bucket.getAggregations().get("aggAttrValue");
            List<? extends Terms.Bucket> aggAttrValueBuckets = aggAttrValue.getBuckets();
            if (aggAttrValueBuckets != null && aggAttrValueBuckets.size() > 0) {

                List<String> attrValueList = aggAttrValueBuckets.stream().map(aggAttrValueBucket -> {
                    //获取每个值的名称
                    return aggAttrValueBucket.getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(attrValueList);
            }
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
        return list;
    }
    //解析品牌的结果
    private List<SearchResponseTmVo> getAggTmResult(Aggregations aggregations) {
        //通过别名获取品牌的id的聚合结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");

        if (aggTmId == null) {
            return null;
        }

        //遍历获取每个品牌的id和子聚合的结果
        List<? extends Terms.Bucket> buckets = aggTmId.getBuckets();
       return buckets.stream().map(bucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌的id
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取字聚合的结果
            ParsedStringTerms aggTmName =bucket.getAggregations().get("aggTmName");
           List<? extends Terms.Bucket> tmNameBuckets = aggTmName.getBuckets();
           if (tmNameBuckets != null && tmNameBuckets.size() > 0) {
                //获取品牌的名字
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            ParsedStringTerms aggTmLogoUrl = bucket.getAggregations().get("aggTmLogoUrl");

            List<? extends Terms.Bucket> tmLogoUrlBuckets = aggTmLogoUrl.getBuckets();
            if (tmLogoUrlBuckets != null && tmLogoUrlBuckets.size() > 0) {
                //获取品牌的图片url
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            return searchResponseTmVo;
        }).collect(Collectors.toList());


    }




    //拼接条件  关键词查询
    private SearchRequest buildRequestParams(Map<String, String> searchData) {
        //searchRequest
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //关键词查询
        String keywords = searchData.get("keywords");
        if(!StringUtils.isEmpty(keywords)){
//            searchSourceBuilder.query(QueryBuilders.matchQuery("title",keywords));
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keywords));
//            //设置查询条件
//            searchRequest.source(searchSourceBuilder);
        }

        //三级分类查询
        String category3Id = searchData.get("category3Id");
        if(!StringUtils.isEmpty(category3Id)){
//            searchSourceBuilder.query(QueryBuilders.matchQuery("category3Id",category3Id));
            boolQueryBuilder.must(QueryBuilders.termQuery("category3Id",category3Id));
//            //设置查询条件
//            searchRequest.source(searchSourceBuilder);
        }


        //品牌条件 1:华为
        String tradeMark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(tradeMark)) {
            String[] split = tradeMark.split(":");
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId",split[0]));
        }else{
            //  品牌不为空则不用聚合条件查询出来  因为就一个品牌   不为空才需要出来   设置聚合条件 品牌聚合
            searchSourceBuilder.aggregation(AggregationBuilders.terms("aggTmId").field("tmId")
                    .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                    .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                    .size(100));
        }



        //平台属性：一个或者多个
        searchData.entrySet().stream().forEach(entry->{
            String key = entry.getKey();

                if(key.startsWith("attr_")){
                    String value = entry.getValue();
                    //切分 429：1匹
                    String[] split = value.split(":");
                    //拼接条件
                    BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("attrs.attrId", split[0])
                    ).must(QueryBuilders.termQuery("attrs.attrValue", split[1]));

                    //设置nested对象的查询条件
                    boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs",nestedBoolQueryBuilder
                    ,ScoreMode.None));
                }
        });



        //价格条件 1000-2000 2000以上
        String price = searchData.get("price");
        if (!StringUtils.isEmpty(price)) {
            //去掉中文
            price = price.replace("元", "").replace("以上", "");
            String[] split = price.split("-");
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));

            //判断是否有第二个值
            if(split.length > 1){
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(split[1]));

            }


        }



        //排序条件  哪个域？ 什么规则？ 有无默认排序？哪种？
        String sortFiled = searchData.get("sortFiled");
        String sortRule = searchData.get("sortRule");
        //有排序规则查询条件
        if(!StringUtils.isEmpty(sortFiled) && !StringUtils.isEmpty(sortRule)){
            searchSourceBuilder.sort(sortFiled,SortOrder.valueOf(sortRule));
        }else{
            //无排序规则查询条件  走默认规则 id 降序
            searchSourceBuilder.sort("id", SortOrder.DESC);
        }



        //分页条件
        Integer size = 100; //默认分100页  京东也这样
        searchSourceBuilder.size(size);

        //获取当前页码
        Integer pageNum = getPageNum(searchData.get("pageNum"));
        searchSourceBuilder.from((pageNum-1)*size);


        //存储条件
        searchSourceBuilder.query(boolQueryBuilder);


//        //设置聚合条件 品牌聚合
//        SearchSourceBuilder aggregation = searchSourceBuilder.aggregation(AggregationBuilders.terms("aggTmId").field("tmId")
//                .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
//                .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
//                .size(100));


        //设置聚合条件 平台属性聚合
        searchSourceBuilder.aggregation(AggregationBuilders.nested("aggAttrs","attrs")
                .subAggregation(AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                        .size(100)));




        //设置查询条件
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }


    //获取分页的当前页码
    private Integer getPageNum(String pageNum) {

        try {
            //可以发生异常 如果用户输入非数字页码
            int i = Integer.parseInt(pageNum);
            if( i <= 0 ){
                return 1;
            }else{
                return i;
            }
        } catch (NumberFormatException e) {
            //发生异常返回默认页面 1页
            return 1;
        }


    }
}
