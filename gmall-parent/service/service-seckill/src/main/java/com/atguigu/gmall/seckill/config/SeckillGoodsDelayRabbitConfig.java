package com.atguigu.gmall.seckill.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SeckillGoodsDelayRabbitConfig
 * @Description 秒杀商品库存同步使用的延迟队列配置
 * @Author yzchao
 * @Date 2022/11/15 20:38
 * @Version V1.0
 */
@Configuration
public class SeckillGoodsDelayRabbitConfig {

    /**
     * 正常交换机
     * @return
     */
    @Bean("seckillGoodsNormalExchange")
    public Exchange seckillGoodsNormalExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_normal_exchange").build();
    }

  /**
   * @Description 死信队列
   * @Date 21:06 2022/11/15
   * @Param []
   * @return org.springframework.amqp.core.Queue
   */
    @Bean("seckillGoodsDeadQueue")
    public Queue seckillGoodsDeadQueue(){
        return QueueBuilder.durable("seckill_goods_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_goods_dead_exchange")
                .withArgument("x-dead-letter-routing-key","seckill.goods.normal")
                .build();
    }

    /**
     * @Description 正常交换机和私信队列的绑定
     * @Date 21:04 2022/11/15
     * @Param [seckillGoodsNormalExchange, seckillGoodsDeadQueue]
     * @return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding seckillGoodsDeadBinding(@Qualifier("seckillGoodsNormalExchange") Exchange seckillGoodsNormalExchange
                    ,@Qualifier("seckillGoodsDeadQueue") Queue seckillGoodsDeadQueue){
        return BindingBuilder.bind(seckillGoodsDeadQueue).to(seckillGoodsNormalExchange).with("seckill.goods.dead").noargs();
    }


    /**
     * @Description 死信交换机
     * @Date 21:05 2022/11/15
     * @Param []
     * @return org.springframework.amqp.core.Exchange
     */
    @Bean("seckillGoodsDeadExchange")
    public Exchange seckillGoodsDeadExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_dead_exchange").build();
    }


    /**
     * @Description 正常队列
     * @Date 21:06 2022/11/15
     * @Param []
     * @return org.springframework.amqp.core.Queue
     */
    @Bean("seckillGoodsNormalQueue")
    public Queue seckillGoodsNormalQueue(){
        return QueueBuilder.durable("seckill_goods_normal_queue").build();
    }


    /**
     * @Description 死信交换机和正常队列的绑定
     * @Date 21:07 2022/11/15
     * @Param []
     * @return org.springframework.amqp.core.Queue
     */
    @Bean
    public Binding seckillGoodsNormalBinding(@Qualifier("seckillGoodsDeadExchange") Exchange seckillGoodsDeadExchange,
                                            @Qualifier("seckillGoodsNormalQueue") Queue seckillGoodsNormalQueue){
        return BindingBuilder.bind(seckillGoodsNormalQueue).to(seckillGoodsDeadExchange)
                .with("seckill.goods.normal").noargs();
    }


    }



