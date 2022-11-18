package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.Java0500GmallCache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ItemController
 * @Description 商品详情页面使用的控制层
 * @Author yzchao
 * @Date 2022/10/26 18:59
 * @Version V1.0
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    
    /**
     * @Description 查询指定skuId的skuInfo的平台属性
     * @Date 20:54 2022/11/2
     * @Param [id]
     * @return java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    @GetMapping("/getBaseAttrInfoBySkuId/{id}")
    @Java0500GmallCache(prefix = "getBaseAttrInfoBySkuId:")
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(@PathVariable Long id){
        return itemService.getBaseAttrInfoBySkuId(id);
    }

    /**
     * @Description 远程调用微服务查询品牌信息
     * @Date 20:31 2022/11/2
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getBaseTradeMark/{id}")
    @Java0500GmallCache(prefix = "getBaseTradeMark:")
    public BaseTrademark getBaseTradeMark(@PathVariable Long id){
        return itemService.getBaseTrademark(id);

    }

    /**
     * @Description 远程调用微服务查询skuInfo信息
     * @Date 18:56 2022/10/26
     * @Param [skuId]
     * @return com.atguigu.gmall.model.product.SkuInfo
     */
    @GetMapping("/getSkuInfo/{skuId}")
    @Java0500GmallCache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        return itemService.getSkuInfo(skuId);
    }

    /**
     * @Description 分类信息查询 根据三级分类ID 查询一级分类 二级分类 三级分类
     * @Date 20:54 2022/10/26
     * @Param [category3Id]
     * @return com.atguigu.gmall.model.product.BaseCategoryView
     */
    @GetMapping("/getBaseCategoryView/{category3Id}")
    @Java0500GmallCache(prefix = "getBaseCategory:")
    public BaseCategoryView getBaseCategory(@PathVariable Long category3Id){
        return itemService.getBaseCategory(category3Id);
    }

    /**
     * @Description 远程调用微服务查询skuImage图片信息
     * @Date 21:20 2022/10/26
     * @Param [skuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getSkuImageList/{skuId}")
    @Java0500GmallCache(prefix = "SkuImageList:")
    public List<SkuImage> SkuImageList(@PathVariable Long skuId){
        return itemService.SkuImageList(skuId);
    }

    /**
     * @Description 远程调用查询sku价格
     * @Date 22:36 2022/10/26
     * @Param [skuId]
     * @return java.math.BigDecimal
     */
    @GetMapping("/getSkuInfoPrice/{skuId}")
    @Java0500GmallCache(prefix = "getSkuInfoPrice:")
    public BigDecimal getSkuInfoPrice(@PathVariable Long skuId){
        return itemService.getSkuInfoPrice(skuId);
    }

    /**
     * @Description 查询销售属性名称和值列表同时标识出当前选中的值是哪几个的sql实现
     * @Date 10:13 2022/10/27
     * @Param [skuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("getSpuAttrBySpuIdAndSkuId/{skuId}/{spuId}")
    @Java0500GmallCache(prefix = "getSpuAttrBySpuIdAndSkuId:")
    public List<SpuSaleAttr> getSpuAttrBySpuIdAndSkuId(@PathVariable Long skuId,@PathVariable Long spuId){
        return itemService.getSpuAttrBySpuIdAndSkuId(skuId,spuId);
    }

    /**
     * @Description 键值对的查询实现
     * @Date 11:53 2022/10/27
     * @Param [spuId]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    @GetMapping("/getSkuValuesList/{spuId}")
    @Java0500GmallCache(prefix = "getSkuValuesList:")
    public Map<Object, Object> getSkuValuesList(@PathVariable Long spuId) {
        Map<Object, Object> skuValuesList = itemService.getSkuValuesList(spuId);
        return skuValuesList;
    }

    /**
     * @Description 扣减库存
     * @Date 18:27 2022/11/13
     * @Param [decountMap]
     * @return void
     */
    @GetMapping("/decountStock")
    public void decountStock(@RequestParam Map<String, String> decountMap){
        itemService.decountStock(decountMap);
    }


    /**
     * @Description 回滚库存
     * @Date 18:27 2022/11/13
     * @Param [decountMap]
     * @return void
     */
    @GetMapping("/rollBackStock")
    public void rollBackStock(@RequestParam Map<String, String> rollBackMap){
        itemService.rollBackStock(rollBackMap);
    }


}
