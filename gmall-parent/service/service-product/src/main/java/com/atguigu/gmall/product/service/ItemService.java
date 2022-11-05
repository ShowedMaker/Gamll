package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @InterfaceName ItemService
 * @Description 商品详情页面使用的接口
 * @Author yzchao
 * @Date 2022/10/26 18:39
 * @Version V1.0
 */

public interface ItemService {

    /**
     * @Description 远程调用微服务查询skuInfo信息
     * @Date 18:56 2022/10/26
     * @Param [skuId]
     * @return com.atguigu.gmall.model.product.SkuInfo
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * @Description 使用缓存优化查询
     * @Date 20:41 2022/10/28
     * @Param [skuId]
     * @return com.atguigu.gmall.model.product.SkuInfo
     */
    SkuInfo getSkuInfoFromRedisOrMysql(Long skuId);

    /**
     * @Description 远程分类信息查询 根据三级分类ID 查询一级分类 二级分类 三级分类
     * @Date 20:50 2022/10/26
     * @Param [category3Id]
     * @return com.atguigu.gmall.model.product.BaseCategoryView
     */
    BaseCategoryView getBaseCategory(Long category3Id);

    /**
     * @Description 远程调用微服务查询skuImage图片信息
     * @Date 21:15 2022/10/26
     * @Param [skuId]
     * @return java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    List<SkuImage> SkuImageList(Long skuId);

    /**
     * @Description 远程调用查询sku价格
     * @Date 22:33 2022/10/26
     * @Param [skuId]
     * @return java.math.BigDecimal
     */
    BigDecimal getSkuInfoPrice(Long skuId);

    /**
     * @Description 查询销售属性名称和值列表同时标识出当前选中的值是哪几个的sql实现
     * @Date 10:10 2022/10/27
     * @Param [skuId, spuId]
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuAttrBySpuIdAndSkuId(Long skuId,Long spuId);

    /**
     * @Description 键值对的查询实现
     * @Date 11:51 2022/10/27
     * @Param [spuId]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
     Map<Object,Object> getSkuValuesList(Long spuId);


     /**
      * @Description 查询品牌
      * @Date 20:29 2022/11/2
      * @Param [id]
      * @return com.atguigu.gmall.model.product.BaseTrademark
      */
     BaseTrademark getBaseTrademark(Long id);

     /**
      * @Description 查询指定skuId的skuInfo的平台属性
      * @Date 20:50 2022/11/2
      * @Param []
      * @return java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
      */
     List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId);

}
