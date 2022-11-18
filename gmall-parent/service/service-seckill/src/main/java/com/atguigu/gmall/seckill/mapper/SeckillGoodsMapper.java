package com.atguigu.gmall.seckill.mapper;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @InterfaceName SeckillGoodsMapper
 * @Description 秒杀商品的mapper的映射
 * @Author yzchao
 * @Date 2022/11/14 18:40
 * @Version V1.0
 */


@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    /**
     * @Description 同步库存
     * @Date 23:34 2022/11/15
     * @Param [goodsId, stock]
     * @return void
     */
    @Update("update seckill_goods set stock_count = #{stockCount} where id = #{goodsId}")
    int updateSeckillGoodsStock(Long goodsId, Integer stockCount);
}
