package com.atguigu.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillOrderMapper;
import com.atguigu.gmall.seckill.pojo.SeckillOrder;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.atguigu.gmall.seckill.util.DateUtil;
import com.sun.javafx.sg.prism.NGAmbientLight;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName SeckillOrderServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/14 20:03
 * @Version V1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class SeckillOrderServiceImpl implements SeckillOrderService {


    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private SeckillOrderMapper seckillOrderMapper;
    @Resource
    private RedissonClient redissonClient;

    /**
     * @param time
     * @param goodsId
     * @param num
     * @return void
     * @Description 新增秒杀订单
     * @Date 19:54 2022/11/14
     * @Param [time, goodsId, num]
     */
    @Override
    public UserRecode addSeckillOrder(UserRecode userRecode) {

        // 用户 时间段  商品id 要买的数量
        String username = "lipei";
        userRecode.setUsername(username);

        //记录用户的排队次数 只放过第一个排队  清理key的情况：a.下单成功(不) b.取消订单(主动，超时) 清理 c.付钱 清理 d.下单失败 清理
        Long increment = redisTemplate.opsForValue().increment("User_Queue_Count_" + username, 1);
        if(increment > 1){
            userRecode.setStatus(3);
            userRecode.setMsg("重复排队，秒杀失败！");
            return userRecode;
        }

        userRecode.setCreateTime(new Date());
        userRecode.setStatus(1);
        userRecode.setMsg("请稍等，你正在排队中！");

        //将用户的排队状态记录到redis中去  与返回给用户 不冲突 可以异步 子线程去实现 最终会完成
        CompletableFuture.runAsync(()->{
            redisTemplate.opsForValue().set("User_Recode_" + username,userRecode);

            //排队成功和下单是两回事 异步 要保证相对公平(排队，薛峰 解耦) 用mq
            //消息可能发失败 会有异常(mq出问题) 只要交换机路由键不错 消息都会进去
            rabbitTemplate.convertAndSend("seckill_order_exchange",
                    "seckill.order.add",
                    JSONObject.toJSONString(userRecode));

        },threadPoolExecutor).whenCompleteAsync((a,b) ->{
             //出异常 假设是rabbitmq的一次异常 消息发不过去  不设计redis异常 redis要崩 上面就崩了
            if(b != null){
                //保存用户排队状态或发送下单消息失败出现异常,需要将用户的排队标识位清理,防止用户不能下单
                /*
                    如果做了rabbit的可靠性投递 就在 confirm和return中写这段逻辑
                 */
                redisTemplate.delete("User_Queue_Count_" + username);
                userRecode.setStatus(3);
                userRecode.setMsg("秒杀失败！请重试！");
                redisTemplate.opsForValue().set("User_Recode_" + username,userRecode);
            }
        });

        //将排队状态返回给用户
        return userRecode;
    }


    /**
     * @return com.atguigu.gmall.seckill.pojo.UserRecode
     * @Description 根据用户名查询 用户排队状态userRecode
     * @Date 20:55 2022/11/14
     * @Param []
     */
    @Override
    public UserRecode getUserRecode() {
        String username = "lipei";
        return (UserRecode) redisTemplate.opsForValue().get("User_Recode_" + username);
    }


    /**
     * @param userRecodeString
     * @return void
     * @Description 只有消费者调 真实的秒杀下单
     * @Date 22:24 2022/11/14
     * @Param [s]
     */
    @Override
    public void listenerAddSeckillOrder(String userRecodeString) {
        //反序列化 将字节流转换成 Java 对象的过程。
        UserRecode userRecode = JSONObject.parseObject(userRecodeString, UserRecode.class);
        //获取时间段
        String time = userRecode.getTime();
        //获取商品ID
        String goodsId = userRecode.getGoodsId();
        //获取数量
        Integer num = userRecode.getNum();
        String username = userRecode.getUsername();


        //判断时间段是否正确
        String nowTime = DateUtil.data2str(DateUtil.getDateMenus().get(0), DateUtil.PATTERN_YYYYMMDDHH);

        if (!nowTime.equals(time)) {
            //时间不对 秒杀商品的时间段还没开始 或者已经结束 秒杀失败
            //设置用户的排队信息
            userRecode.setStatus(3);
            userRecode.setMsg("时间不对 秒杀商品的时间段还没开始 或者已经结束 秒杀失败！");
            //更新
            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            //清空锁  防止用户不能再下其他的订单
            redisTemplate.delete("User_Queue_Count_" + username);

            return;
        }


        //时间正确
        //根据time和goodsId获取商品的数据
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
        //设置用户的排队信息
        if (seckillGoods == null) {
            //商品不在redis中存在了  买完了不从redis中拿掉   活动时间到了 拿掉
            userRecode.setStatus(3);
            userRecode.setMsg("商品已经过期 秒杀失败！");
            //更新
            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            //清空锁  防止用户不能再下其他的订单
            redisTemplate.delete("User_Queue_Count_" + username);

            return;
        }


//        //获取商品的限购
        Integer seckillLimit = seckillGoods.getSeckillLimit();
        if (seckillLimit < num) {
            //超出限购
            userRecode.setStatus(3);
            userRecode.setMsg("超出商品的限购数量，每个商品限购" + seckillLimit + "个， 秒杀失败！");
            //更新
            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            //清空锁  防止用户不能再下其他的订单
            redisTemplate.delete("User_Queue_Count_" + username);

            return;
        }


        //时间对 商品有 没超限购  库存是否充足 TODO -- 1.超卖问题 2.库存减了以后商品数据没有更新 3.用户查询的商品剩余库存可能错
        //从 redis的list中 每次只出列一个 rightPop  只要取出结果不为空 就说明有库存  可以卖 没有用锁
        //方案一
        for (int i = 0; i < num; i++) {
            Object rightPop = redisTemplate.opsForList().rightPop("Seckill_Goods_Stock_Queue_" + goodsId);
            if (rightPop == null) {
                //判断是否需要回滚
                if (i > 0) { //取出来大于等于一个
                    //需要回滚
                    String[] ids = getIds(i, goodsId);
                    redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + goodsId, ids);
                }

                //库存不足
                userRecode.setStatus(3);
                userRecode.setMsg("商品库村不足，秒杀失败！");
                //更新
                redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
                //清空锁  防止用户不能再下其他的订单
                //保存用户排队状态或发送下单消息失败出现异常,需要将用户的排队标识位清理,防止用户不能下单
                redisTemplate.delete("User_Queue_Count_" + username);
                return;
            }
        }


        //走到了 库组足够 不会超卖 做库存自增 value值为-num 相当于减库存 用户买了多少num个 立刻补充num个
        Long increment = redisTemplate.opsForHash().increment("Seckill_Goods_Increment_" + time, goodsId, -num);
        //方案二  //出现复数  说明空间不足
        if(increment < 0){

            //回滚
            redisTemplate.opsForHash().increment("Seckill_Goods_Increment_" + time, goodsId, num);
            //库存不足
            userRecode.setStatus(3);
            userRecode.setMsg("商品库村不足，秒杀失败！");
            //更新
            redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);
            //清空锁  防止用户不能再下其他的订单
            //保存用户排队状态或发送下单消息失败出现异常,需要将用户的排队标识位清理,防止用户不能下单
            redisTemplate.delete("User_Queue_Count_" + username);
            return;
        }

        //想要精确 直接查这个key "Seckill_Goods_Increment_" + time 允许有点误差 下面set进去
        seckillGoods.setStockCount(increment.intValue());
        //更新商品的库存
        redisTemplate.opsForHash().put(time,goodsId,seckillGoods);

        //生成秒杀订单 给用户看的  要写进redis中给他查  钱付完之后 改数据库 删redis
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(UUID.randomUUID().toString().replace("-",""));
        seckillOrder.setGoodsId(goodsId);
        seckillOrder.setNum(num);
        seckillOrder.setMoney(seckillGoods.getCostPrice().multiply
                                (new BigDecimal(num)).intValue()+"");
        seckillOrder.setUserId(username);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");

        //写入数据库(优先)和redis
        redisTemplate.opsForHash().put("Seckill_Order_" + time,seckillOrder.getId(),seckillOrder);
       //redis有 没必要操作数据库 只有取消和付钱的时候入库 其他不需要入库

        //订单生成好 修改用户排队状态
        userRecode.setStatus(2);
        userRecode.setMsg("秒杀成功！，等待付款");
        userRecode.setOrderId(seckillOrder.getId());
        userRecode.setMoney(seckillOrder.getMoney());

        //更新
        redisTemplate.opsForValue().set("User_Recode_" + username, userRecode);

        //锁先不用删  现在没付款或取消订单 还不能买其他东西
        //发送延迟消息：5-10-15分钟 --TODO---
        rabbitTemplate.convertAndSend("seckill_order_normal_exchange",
                                      "seckill.order.dead"
                                      ,username //username存在排队状态 右面有全部内容
                                      ,message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setExpiration("900000");
                    return message;
                });





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

    /**
     * @param username
     * @return void
     * @Description 取消秒杀订单 主动取消 和超时取消
     * @Date 16:37 2022/11/16
     * @Param [username]
     */
    @Override
    public void cancelSeckillOrder(String username) {

        //判断是超时取消  还是主动取消  超时取消本地线程池没有username,只能从参数里拿  主动取消有
        String msg = "主动取消";
        if(!StringUtils.isEmpty(username)){
            //超时取消没有令牌
            username = "lipei";
            msg = "超市取消";
        }

        //控制用户重复取消秒杀订单
        RLock lock = redissonClient.getLock("Cancel_Seckill_Order_Lock_" + username);
        //枪锁 保证同一个用户只接受一个请求去秒杀订单
        if(lock.tryLock()){
            try {
                //从redis中获取用户的排队信息 userRecode 里面有 time seckillOrder.getId() seckillOrder
                UserRecode userRecode = (UserRecode) redisTemplate.opsForValue().get("User_Recode_" + username);
                if (userRecode != null) {
                    //获取时间段
                    String time = userRecode.getTime();
                    //获取订单号
                    String orderId = userRecode.getOrderId();
                    //从redis中取出订单
                    SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForHash().get("Seckill_Order_" + time, orderId);
                    //取消订单 将订单的状态修改为:取消 a b
                    seckillOrder.setStatus(msg);

                    //将数据写入数据库
                    int insert = seckillOrderMapper.insert(seckillOrder);
                    if (insert <= 0) {
                        throw new GmallException(msg + "订单失败");
                    }
                    //回滚库存
                    rollbackGoodsStock(time, userRecode.getGoodsId(), userRecode.getNum());
                    //删除redis 新增订单产生的所有key
                    deleteRedisUserFlag(username, time, seckillOrder.getId());
                }
              }catch (Exception e){
                log.error("取消订单失败，失败的原因为" + e.getMessage());
            }finally {
                lock.unlock();
            }
        }else{
            //重复取消
            return;
        }
    }

    
    /**
     * @Description 回滚库存
     * @Date 18:14 2022/11/16
     * @Param []
     * @return void
     */
    private void rollbackGoodsStock(String time,String goodsId,Integer num) {
        //从redis中获取商品的数据
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
        //回滚后的商品真是剩余库存
        Long increment = redisTemplate.opsForHash().increment("Seckill_Goods_Increment_" + time, goodsId, num);
        if(seckillGoods != null){
        //商品活动没有结束 : 下单时做了什么改变 回滚就回哪些 需要回滚商品数据  商品队列   商品库存自增值不用
            //需要回滚商品数据
            seckillGoods.setStockCount(increment.intValue());
            redisTemplate.opsForHash().put(time, goodsId,seckillGoods);
            //商品队列
            String[] ids = getIds(num, goodsId);
            redisTemplate.opsForList().leftPushAll("Seckill_Goods_Stock_Queue_" + goodsId,ids);

        }


        //全部回滚完  结束






    }


    /**
     * @Description 清理用户的标识位：排队计数器 "User_Queue_Count_"
     *                             排队状态   "User_Recode_"
     *                             用户订单信息 "Seckill_Order_"
     * @Date 18:11 2022/11/16
     * @Param [username, time, orderId]
     * @return void
     */
    private void deleteRedisUserFlag(String username, String time, String orderId) {
        redisTemplate.delete("User_Queue_Count_" + username);
        redisTemplate.delete("User_Recode_" + username);
        redisTemplate.opsForHash().delete("Seckill_Order_" + time, orderId);
    }
}
