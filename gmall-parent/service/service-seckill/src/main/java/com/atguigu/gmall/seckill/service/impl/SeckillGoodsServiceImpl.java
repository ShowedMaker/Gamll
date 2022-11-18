package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.sun.media.jfxmedia.logging.Logger;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SeckillGoodsServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/14 19:23
 * @Version V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class SeckillGoodsServiceImpl implements SeckillGoodsService {


    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @param time
     * @return java.util.List<com.atguigu.gmall.model.activity.SeckillGoods>
     * @Description 查询指定时间段的redis中的秒杀商品
     * @Date 19:22 2022/11/14
     * @Param [time]
     */
    @Override
    public List<SeckillGoods> getSeckillGoodsList(String time) {
        //通过redis的Hash类型获取
        //泛型擦除 只有的用的时候才强转
        List values = redisTemplate.opsForHash().values(time);
        return values;
    }


    /**
     * @param time
     * @param goodsId
     * @return com.atguigu.gmall.model.activity.SeckillGoods
     * @Description 查询指定时间段的指定goodsId的redis中的秒杀商品
     * @Date 19:41 2022/11/14
     * @Param [time, orderId]
     */
    @Override
    public SeckillGoods getSeckillGoods(String time, String goodsId) {
        return (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
    }

    /**
     * @param time
     * @return void
     * @Description 同步数据到数据库中去
     * @Date 22:42 2022/11/15
     * @Param [time]
     */
    @Override //消费者掉的
    public void updateSeckillGoodsStock(String time) {
        //从redis中获取该时间段的全部商品的ID数据 通过自增值获取
        Set<String> keys = redisTemplate.opsForHash().keys("Seckill_Goods_Increment_" + time);
        //得到keys为所有商品的id集合 不重复
        if(keys != null && keys.size() > 0){
            //遍历同步每个商品的剩余库存数据到数据库中去
            keys.stream().forEach( goodsId ->{
                    //获取redis中的剩余库存
                Integer stockCount = (Integer) redisTemplate.opsForHash().get("Seckill_Goods_Increment_" + time, goodsId);
                //同步到数据库
                int integer = seckillGoodsMapper.updateSeckillGoodsStock(Long.valueOf(goodsId),stockCount);
                if(integer < 0) {
                    //1.使用定时任务扫描失败同步的日志,再次进行同步直到成功为止,但是若15次或20次都同步失败,
                    // 2.发送短信/邮件通知人工处理
                    log.error("商品库存同步失败,所属的时间段为:" + time
                            + ",失败的商品的id为:" + goodsId + ", 应该同步的剩余库存为:" + stockCount);
                }

                //哪个同步成功就删除哪个
                redisTemplate.opsForHash().delete(goodsId);
            });
        }




    }
}

