package com.atguigu.gmall.seckill.task;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.bouncycastle.math.Primes;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AddSeckillGoods
 * @Description 将数据库中秒杀商品的数据提前预热到redis中去的定时任务
 * @Author yzchao
 * @Date 2022/11/14 17:00
 * @Version V1.0
 */
@Component
public class AddSeckillGoodsFromDBToRedisTask {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    @Resource
    private RedisTemplate redisTemplate;
    /*
      定时任务
      cron:秒 分 时 日 月 周 年(省略) 需要在启动类上开启定时任务注解
      将数据库中秒杀商品的数据提前预热到redis中
      * 任意时间
      ?:忽略这个时间
      直接写数字，在指定的时间执行
      逗号分割
      区间
      / 间隔执行
      fixedDelay 每5秒执行一次 受方法的执行时间影响 -->上一次方法执行完多久开始下一次
      fixedRate 每5秒执行一次 不受方法的执行时间影响 -->上一次开始执行多久以后执行下一次 可能出现两个(或多个)任务一起跑
      initialDelay 只影响第一次什么时候执行  5 项目启动5秒后执行一次
     */
    @Scheduled(cron = "1/20 * * * * *")
//    @Scheduled(fixedDelay = 3000)
    public void addSeckillGoodsFromDBToRedis(){
        //计算当前这个时间段所在的时间段，以及后面的4个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //循环查询每个时间段的开始时间
        dateMenus.stream().forEach(date -> {
            //计算当前的这个时间段的开始时间：2022-11-14 18:00:00
            String startTime = DateUtil.data2str(date, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //计算当前的这个时间段的截至时间：2022-11-14 20:00:00
            String endTime = DateUtil.data2str(DateUtil.addDateHour(date, 2), DateUtil.PATTERN_YYYY_MM_DDHHMM);
            Date endTimes = DateUtil.addDateHour(new Date(), 2);
            //获取商品key在redis中的有效时间
            long liveTime = endTimes.getTime() - System.currentTimeMillis();

            //计算redis中存储时间段商品的key ：2022111418
            String key = DateUtil.data2str(date, DateUtil.PATTERN_YYYYMMDDHH);


            //拼接查询这个时间段商品的条件
            LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
            //商品必须是审核状态为  "1" 审核通过的
            wrapper.eq(SeckillGoods::getStatus,"1");
            //商品必须在活动时间以内 startTime<=start_time
            //gt 就是 greater than大于
            //lt 就是 less than小于
            //ge 就是 greater than or equal 大于等于
            //le 就是 less than or equal 小于等于
            wrapper.ge(SeckillGoods::getStartTime,startTime);
            //商品必须在活动时间以内 end_time<=endTime
            wrapper.le(SeckillGoods::getEndTime,endTime);
            //剩余库存必须大于0
            wrapper.gt(SeckillGoods::getStockCount,0);
            //redis中没有的查出来才放进去  有的就不用了
            //下面set集合是redis中有的
            Set keys = redisTemplate.opsForHash().keys(key);
            //判断
            if(keys != null && keys.size() >0){
                //取得不在这个集合中的key
                wrapper.notIn(SeckillGoods::getId,keys);
            }
            //查寻
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(wrapper);
            //遍历将商品数据写入redis
            seckillGoodsList.stream().forEach(seckillGoods -> {
                //将商品数据写入redis 注意用redis的Hash类型
                //TODO 1.商品活动到期  需要清理掉 2.商品活动到期需要将剩余库存同步到数据库

                redisTemplate.opsForHash().put(key,seckillGoods.getId()+"",seckillGoods);
                //把商品放进redis的list队列中 构建一个商品库存剩余个数的长度的队列
                Integer stockCount = seckillGoods.getStockCount();
                //构建好商品库存剩余个数的数组，并且数组的每一个位置 都存储一个id
                String[] ids = getIds(stockCount,seckillGoods.getId()+"");
                //下单能否完成的依据 用户能从这里拿到数据就可以下单
                redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + seckillGoods.getId(),ids);
                redisTemplate.expire("Seckill_Goods_Stock_Queue_" + seckillGoods.getId(),liveTime,TimeUnit.MILLISECONDS);
                // 构建一个商品库存的自增值 每个商品只会执行一次 前面判断了keys != null size > 0  只有第一次写进redis中的商品值可以执行这一次
                redisTemplate.opsForHash().increment("Seckill_Goods_Increment_" + key,seckillGoods.getId()+"",stockCount);
            });


            //设置商品相关key的过期时间
            setSeckillGoodsKeysRedisExpire(liveTime,key);
        });
    }


    /**
     * @Description 设置商品相关key的过期时间
     * @Date 20:07 2022/11/15
     * @Param [liveTime, key]
     * @return void
     */
    private void setSeckillGoodsKeysRedisExpire(long liveTime, String key) {

        //保证每个时间段的数据只设置一次过期时间
        Long result = redisTemplate.opsForHash().
                increment("Seckill_Goods_Expire_Times", key, 1);

        if(result > 1){
            return;
        }

        //设置商品的数据过期
        redisTemplate.expire(key,liveTime, TimeUnit.MILLISECONDS);
        //设置商品库存自增值的数据过期
        //同步数据库 以这个为准 商品活动结束后 先不同步 用户不能买 但是可以退 退了要加回去 再也没有人改动时  同步  9:59 -> 14:59 15分钟有效期 不能动  加liveTime+1800000 确保没有任何人操作这个数据库
        //redisTemplate.expire("Seckill_Goods_Increment_" + key,liveTime+1800000, TimeUnit.MILLISECONDS); //1800> 15分钟的订单超时过期时间取消

        //第二种方法 不设置自增值的过期  确定该什么时候同步数据库库存 使用mq 发私信队列  TTL 延迟队列
        rabbitTemplate.convertAndSend("seckill_goods_normal_exchange",
                "seckill.goods.dead",
                key,
                (message ->{
                    MessageProperties messageProperties = message.getMessageProperties();
                    //设置过期时间：活动结束半小时后收到商品同步消息
//                    messageProperties.setExpiration((liveTime+1800000) + "");
                    messageProperties.setExpiration(20000 + "");
                    return message;
                }));

    }


    /**
     * @Description 构建商品库存数组
     * @Date 18:53 2022/11/15
     * @Param [stockCount, s]
     * @return void
     */
    private String[] getIds(Integer stockCount, String goodsId) {
        //声名一个库存呢长度的数组
        String[] ids = new String[stockCount];
        //给每个元素赋值
        for (Integer i = 0; i < stockCount; i++) {
            ids[i] = goodsId;
        }
        return ids;
    }
}
