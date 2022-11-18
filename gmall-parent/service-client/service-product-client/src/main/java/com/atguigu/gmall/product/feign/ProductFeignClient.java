package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @InterfaceName ProductFeignClient
 * @Description item微服务调用product微服务 feign接口
 * @Author yzchao
 * @Date 2022/10/26 19:13
 * @Version V1.0
 */

@FeignClient(value = "service-product", path = "/api/item",contextId = "ProductFeign")
public interface ProductFeignClient {

    /**
     * @Description 查询指定skuId的skuInfo的平台属性
     * @Date 20:56 2022/11/2
     * @Param [id]
     * @return java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    @GetMapping("/getBaseAttrInfoBySkuId/{id}")
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(@PathVariable Long id);

    /**
     * @Description 查询品牌
     * @Date 20:56 2022/11/2
     * @Param [id]
     * @return com.atguigu.gmall.model.product.BaseTrademark
     */
    @GetMapping("/getBaseTradeMark/{id}")
    public BaseTrademark getBaseTradeMark(@PathVariable Long id);


    /**
     * @return com.atguigu.gmall.model.product.SkuInfo
     * @Description 远程调用微服务查询skuInfo信息
     * @Date 18:58 2022/10/26
     * @Param [skuId]
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId);


    /**
     * @return com.atguigu.gmall.model.product.BaseCategoryView
     * @Description 分类信息查询 根据三级分类ID 查询一级分类 二级分类 三级分类
     * @Date 20:56 2022/10/26
     * @Param [category3Id]
     */
    @GetMapping("/getBaseCategoryView/{category3Id}")
    public BaseCategoryView getBaseCategory(@PathVariable Long category3Id);

    /**
     * @return java.util.List<com.atguigu.gmall.model.product.SkuImage>
     * @Description 远程调用微服务查询skuImage图片信息
     * @Date 21:22 2022/10/26
     * @Param [skuId]
     */
    @GetMapping("/getSkuImageList/{skuId}")
    public List<SkuImage> SkuImageList(@PathVariable Long skuId);

    /**
     * @return java.math.BigDecimal
     * @Description 远程调用查询sku价格
     * @Date 22:37 2022/10/26
     * @Param [skuId]
     */
    @GetMapping("/getSkuInfoPrice/{skuId}")
    public BigDecimal getSkuInfoPrice(@PathVariable Long skuId);

    /**
     * @return com.atguigu.gmall.common.result.Result
     * @Description 查询销售属性名称和值列表同时标识出当前选中的值是哪几个的sql实现
     * @Date 10:13 2022/10/27
     * @Param [skuId]
     */
    @GetMapping("/getSpuAttrBySpuIdAndSkuId/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuAttrBySpuIdAndSkuId(@PathVariable Long skuId,@PathVariable Long spuId);

    /**
     * @Description 键值对的查询实现
     * @Date 13:46 2022/10/27
     * @Param [spuId]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    @GetMapping("/getSkuValuesList/{spuId}")
    public Map<Object, Object> getSkuValuesList(@PathVariable Long spuId);

    /**
     * @Description 扣除库存
     * @Date 0:36 2022/11/10
     * @Param [decountMap]
     * @return void
     */
    @GetMapping("/decountStock")
    public void decountStock(@RequestParam Map<String, String> decountMap);


    /**
     * @Description 回滚库存
     * @Date 18:27 2022/11/13
     * @Param [decountMap]
     * @return void
     */
    @GetMapping("/rollBackStock")
    public void rollBackStock(@RequestParam Map<String, String> rollBackMap);
}