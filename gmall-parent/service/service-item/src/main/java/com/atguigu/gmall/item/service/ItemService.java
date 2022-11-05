package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.product.SkuInfo;

import java.util.Map;

/**
 * @InterfaceName ItemService
 * @Description 商品详情页面使用的接口
 * @Author yzchao
 * @Date 2022/10/26 18:30
 * @Version V1.0
 */

public interface ItemService {


    /**
     * @Description 远程调用查询skuinfo信息
     * @Date 18:42 2022/10/26
     * @Param [skuId]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String,Object> getSkuInfo(Long skuId);
}
