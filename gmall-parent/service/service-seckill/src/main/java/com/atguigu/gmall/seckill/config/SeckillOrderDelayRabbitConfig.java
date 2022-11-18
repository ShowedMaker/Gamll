package com.atguigu.gmall.seckill.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SeckillOrderDelayRabbitConfig
 * @Description 秒杀订单同步使用的延迟队列配置
 * @Author yzchao
 * @Date 2022/11/15 20:38
 * @Version V1.0
 */
@Configuration
public class SeckillOrderDelayRabbitConfig {

    /**
     * 正常交换机
     * @return
     */
    @Bean("seckillOrderNormalExchange")
    public Exchange seckillOrderNormalExchange(){
        return ExchangeBuilder.directExchange("seckill_order_normal_exchange").build();
    }

  /**
   * @Description 死信队列
   * @Date 21:06 2022/11/15
   * @Param []
   * @return org.springframework.amqp.core.Queue
   */
    @Bean("seckillOrderDeadQueue")
    public Queue seckillOrderDeadQueue(){
        return QueueBuilder.durable("seckill_order_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_order_dead_exchange")
                .withArgument("x-dead-letter-routing-key","seckill.order.normal")
                .build();
    }

    /**
     * @Description 正常交换机和私信队列的绑定
     * @Date 21:04 2022/11/15
     * @Param [seckillGoodsNormalExchange, seckillGoodsDeadQueue]
     * @return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding seckillOrderDeadBinding(@Qualifier("seckillOrderNormalExchange") Exchange seckillOrderNormalExchange
                    ,@Qualifier("seckillOrderDeadQueue") Queue seckillOrderDeadQueue){
        return BindingBuilder.bind(seckillOrderDeadQueue).to(seckillOrderNormalExchange).with("seckill.order.dead").noargs();
    }


    /**
     * @Description 死信交换机
     * @Date 21:05 2022/11/15
     * @Param []
     * @return org.springframework.amqp.core.Exchange
     */
    @Bean("seckillOrderDeadExchange")
    public Exchange seckillOrderDeadExchange(){
        return ExchangeBuilder.directExchange("seckill_order_dead_exchange").build();
    }


    /**
     * @Description 正常队列
     * @Date 21:06 2022/11/15
     * @Param []
     * @return org.springframework.amqp.core.Queue
     */
    @Bean("seckillOrderNormalQueue")
    public Queue seckillOrderNormalQueue(){
        return QueueBuilder.durable("seckill_order_normal_queue").build();
    }


    /**
     * @Description 死信交换机和正常队列的绑定
     * @Date 21:07 2022/11/15
     * @Param []
     * @return org.springframework.amqp.core.Queue
     */
    @Bean
    public Binding seckillOrderNormalBinding(@Qualifier("seckillOrderDeadExchange") Exchange seckillOrderDeadExchange,
                                            @Qualifier("seckillOrderNormalQueue") Queue seckillOrderNormalQueue){
        return BindingBuilder.bind(seckillOrderNormalQueue).to(seckillOrderDeadExchange)
                .with("seckill.order.normal").noargs();
    }


    }



