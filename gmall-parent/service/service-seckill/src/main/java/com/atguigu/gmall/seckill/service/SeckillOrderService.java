package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.seckill.pojo.UserRecode;

/**
 * @InterfaceName SeckillOrderService
 * @Description 秒杀订单的接口类
 * @Author yzchao
 * @Date 2022/11/14 19:52
 * @Version V1.0
 */

public interface SeckillOrderService {

    /**
     * @return void
     * @Description 新增秒杀订单
     * @Date 19:54 2022/11/14
     * @Param [time, goodsId, num]
     */
    public UserRecode addSeckillOrder(UserRecode userRecode); // 用户 时间段  商品id 要买的数量

    /**
     * @Description 根据用户名查询 用户排队状态userRecode
     * @Date 20:55 2022/11/14
     * @Param []
     * @return com.atguigu.gmall.seckill.pojo.UserRecode
     */
    public UserRecode getUserRecode();


    /**
     * @Description 只有消费者调 真实的秒杀下单
     * @Date 22:24 2022/11/14
     * @Param [s]
     * @return void
     */
    public void listenerAddSeckillOrder(String userRecodeString);

    /**
     * @Description 取消秒杀订单 主动取消 和超时取消
     * @Date 16:45 2022/11/16
     * @Param [username]
     * @return void
     */
    void cancelSeckillOrder(String username);
}
