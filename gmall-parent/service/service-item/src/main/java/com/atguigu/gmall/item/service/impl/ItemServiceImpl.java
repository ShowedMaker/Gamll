package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ItemServiceImpl
 * @Description 商品详情页面使用的接口的实现类
 * @Author yzchao
 * @Date 2022/10/26 18:32
 * @Version V1.0
 */
@Service
public class ItemServiceImpl implements ItemService {


    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * @Description 远程调用查询skuinfo信息
     * @Date 18:39 2022/10/26
     * @Param [skuId]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object> getSkuInfo(Long skuId) {
        //返回初始化结果
        Map<String, Object> map = new ConcurrentHashMap<>();
        //参数校验
        if (skuId == null) {
            return map;
        }

        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
            //远程调用查询skuInfo信息
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            //判断商品是否存在，若不存在，直接返回结束
            if (skuInfo == null || skuInfo.getId() == null) {
                return null;
            }
            //保存商品的信息
            map.put("skuInfo", skuInfo);
            return skuInfo;
        },threadPoolExecutor);

        CompletableFuture<Void> future2 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null) {
                return;
            }
            //查询分类的信息
            BaseCategoryView baseCategoryView = productFeignClient.getBaseCategory(skuInfo.getCategory3Id());
            map.put("baseCategoryView", baseCategoryView);
        },threadPoolExecutor);


        //查询图片的列表
        CompletableFuture<Void> future3 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null) {
                return;
            }
            List<SkuImage> skuImageList = productFeignClient.SkuImageList(skuInfo.getId());
            map.put("skuImageList", skuImageList);
        },threadPoolExecutor);


        CompletableFuture<Void> future4 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null) {
                return;
            }
            //查询价格
            BigDecimal skuInfoPrice = productFeignClient.getSkuInfoPrice(skuInfo.getId());
            map.put("skuInfoPrice", skuInfoPrice);
        },threadPoolExecutor);

        CompletableFuture<Void> future5 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null) {
                return;
            }
            //销售属性信息 当前sku的销售属性值有几个
            List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuAttrBySpuIdAndSkuId(skuInfo.getId(), skuInfo.getSpuId());
            map.put("spuSaleAttrList", spuSaleAttrList);
        },threadPoolExecutor);

        CompletableFuture<Void> future6 = future1.thenAcceptAsync((skuInfo) -> {
            //判断商品是否存在
            if (skuInfo == null) {
                return;
            }
            //查询页面跳转使用的键值对
            Map<Object, Object> skuValuesList = productFeignClient.getSkuValuesList(skuInfo.getSpuId());
            map.put("skuValuesList", skuValuesList);
        },threadPoolExecutor);

        //等待所有任务完成后返回
        CompletableFuture.allOf(future1,future2,future3,future4,future5,future6).join();
        //将上面6个线程的结果全部整合返回
        return map;
    }
}
