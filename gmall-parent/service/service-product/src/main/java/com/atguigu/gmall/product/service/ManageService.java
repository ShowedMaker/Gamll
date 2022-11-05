package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @InterfaceName ManageService
 * @Description 商品管理的接口类
 * @Author yzchao
 * @Date 2022/10/24 15:13
 * @Version V1.0
 */

public interface ManageService{

    /**
     * @Description 查询所有的一级分类
     * @Date 18:05 2022/10/24
     * @Param []
     * @return java.util.List<com.atguigu.gmall.model.product.BaseCategory1>
     */
    public List<BaseCategory1> getCategory1();

    /**
     * @Description 根据一级分类查询所有的二级分类
     * @Date 18:03 2022/10/24
     * @Param [category1Id]
     * @return java.util.List<com.atguigu.gmall.model.product.BaseCategory2>
     */
    public List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * @Description 根据二级分类查询所有的三级分类
     * @Date 18:03 2022/10/24
     * @Param [category2Id]
     * @return java.util.List<com.atguigu.gmall.model.product.BaseCategory3>
     */
    public List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * @Description 保存平台属性信息
     * @Date 18:16 2022/10/24
     * @Param [baseAttrInfo]
     * @return void
     */
    void save(BaseAttrInfo baseAttrInfo);

    /**
     * @Description 获得平台属性列表
     * @Date 20:13 2022/10/24
     * @Param []
     * @return java.util.List
     */
    List<BaseAttrInfo> getBaseAttrInfo(Long category3Id);

    /**
     * @Description 删除平台属性和值
     * @Date 22:54 2022/10/24
     * @Param [id]
     * @return void
     */
    void deleteBaseAttrInfo(Long id);

    /**
     * @Description 获取添加SPU时品牌值（所有值）
     * @Date 11:56 2022/10/25
     * @Param []
     * @return void
     */
    List<BaseTrademark> getBaseTradeMark();

    /**
     * @Description 后台管理系统销售属性查询
     * @Date 14:12 2022/10/25
     * @Param []
     * @return java.util.List<com.atguigu.gmall.model.product.BaseSaleAttr>
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * @Description 上传spu信息
     * @Date 15:40 2022/10/25
     * @Param [baseSaleAttr]
     * @return void
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * @Description 分页条件查询spuinfo信息
     * @Date 18:28 2022/10/25
     * @Param []
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SpuInfo>
     */
    IPage<SpuInfo> getSpuInfoByPage(Integer page,Integer size,Long category3Id);


    /**
     * @Description 根据spu的id查询这个spu的全部销售属性和每个销售属性对应的值列表
     * @Date 19:25 2022/10/25
     * @Param [spuId]
     * @return void
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);


    /**
     * @Description 根据spu的id查询这个spu的全部图片列表
     * @Date 20:05 2022/10/25
     * @Param [spuId]
     * @return java.util.List<com.atguigu.gmall.model.product.SpuImage>
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * @Description 上传sku信息
     * @Date 21:17 2022/10/25
     * @Param [skuInfo]
     * @return void
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * @Description 分页查询sku的信息
     * @Date 23:38 2022/10/25
     * @Param [page, size]
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SpuInfo>
     */
    IPage<SkuInfo> getSkuInfoByPage(Integer page,Integer size);

    /**
     * @Description 上架或者下架sku商品
     * @Date 13:56 2022/10/26
     * @Param [skuId, status]
     * @return void
     */
    void OnSaleOrCancelSale(Long skuId,Short status);


    /**
     * @Description 新增品牌
     * @Date 14:43 2022/10/26
     * @Param [baseTrademark]
     * @return void
     */
    void addOrUpdateBaseTrademark(BaseTrademark baseTrademark);

    /**
     * @Description 分页查询品牌列表
     * @Date 15:02 2022/10/26
     * @Param [page, size]
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.BaseTrademark>
     */
    IPage<BaseTrademark> getBaseTradeMarkByPage(Integer page,Integer size);

    /**
     * @Description 根据品牌ID查询品牌
     * @Date 15:37 2022/10/26
     * @Param [id]
     * @return com.atguigu.gmall.model.product.BaseTrademark
     */
    BaseTrademark getBaseTradeMarkById(Long id);

    /**
     * @Description 根据品牌ID删除品牌
     * @Date 15:43 2022/10/26
     * @Param [id]
     * @return void
     */
    void removeBaseTradeMark(Long id);
}
