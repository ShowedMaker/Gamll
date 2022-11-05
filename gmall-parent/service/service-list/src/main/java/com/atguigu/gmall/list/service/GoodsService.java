package com.atguigu.gmall.list.service;

/**
 * @InterfaceName GoodsService
 * @Description 商品上下架的接口
 * @Author yzchao
 * @Date 2022/11/2 16:40
 * @Version V1.0
 */

public interface GoodsService {


    /**
     * @Description 上架接口将商品从数据库写入es
     * @Date 16:43 2022/11/2
     * @Param []
     * @return void
     */
    public void goodsFromDBToEs(Long skuId);


    /**
     * @Description 下架接口 从es中移除调商品
     * @Date 16:43 2022/11/2
     * @Param []
     * @return void
     */
    public void goodsRemoveFromEs(Long goodsId);


}
