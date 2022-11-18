package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @InterfaceName SeckillGoodsService
 * @Description 秒杀商品的接口类
 * @Author yzchao
 * @Date 2022/11/14 19:21
 * @Version V1.0
 */

public interface SeckillGoodsService {

    /**
     * @Description 查询指定时间段的redis中的秒杀商品
     * @Date 19:22 2022/11/14
     * @Param [time]
     * @return java.util.List<com.atguigu.gmall.model.activity.SeckillGoods>
     */
    public List<SeckillGoods> getSeckillGoodsList(String time);


    /**
     * @Description 查询指定时间段的指定goodsId的redis中的秒杀商品
     * @Date 19:41 2022/11/14
     * @Param [time, orderId]
     * @return com.atguigu.gmall.model.activity.SeckillGoods
     */
    public SeckillGoods getSeckillGoods(String time,String goodsId);

    /**
     * @Description 同步数据到数据库中去
     * @Date 22:42 2022/11/15
     * @Param [time]
     * @return void
     */
    public void updateSeckillGoodsStock(String time);
}
