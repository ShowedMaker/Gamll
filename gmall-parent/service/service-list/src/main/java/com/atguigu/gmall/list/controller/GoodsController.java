package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName GoodsController
 * @Description 商品上下架控制层
 * @Author yzchao
 * @Date 2022/11/2 19:27
 * @Version V1.0
 */
@RestController
@RequestMapping("/api/list")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;//旧版本的 不用elasticsearchTemplate 这个用的是9300 原始的
    //换成elasticsearchRestTemplate 是走Http请求 restful风格 才能使用配置里的9200
    /**
     * @Description 数据库写入es
     * @Date 23:03 2022/11/2
     * @Param [skuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/goodsFromDBToEs/{skuId}")
    public Result goodsFromDBToEs(@PathVariable Long skuId) {
        goodsService.goodsFromDBToEs(skuId);
        return Result.ok();
    }

    /**
     * @Description 从es中删除去
     * @Date 23:04 2022/11/2
     * @Param [skuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/goodsRemoveFromEs/{skuId}")
    public Result goodsRemoveFromEs(@PathVariable Long skuId) {
        goodsService.goodsRemoveFromEs(skuId);
        return Result.ok();
    }


    /**
     * @Description 生成Goods_java0509的索引和映射  可以省略 写上以防万一 es启动时会创建
     * @Date 23:04 2022/11/2
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/createIndexAndMapping")
    public Result createIndexAndMapping(){
        //elasticsearchRestTemplate.delete(); 映射构建好 要更改 实体和es映射不上 删了再建
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class); //解析Goods中的注解 拼成json 发给es执行 练习已手动拼过
        return Result.ok();
    }


    /**
     * @Description 增加热度值
     * @Date 22:22 2022/11/5
     * @Param [goodsId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/addHotScore")
    public Result addHotScore(Long goodsId){
        goodsService.addHotScore(goodsId);
        return Result.ok();
    }




}
