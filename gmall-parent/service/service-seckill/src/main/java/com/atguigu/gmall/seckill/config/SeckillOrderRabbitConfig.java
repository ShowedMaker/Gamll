package com.atguigu.gmall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SeckillOrderRabbitConfig
 * @Description 秒杀下单排队消息的消息队列配置
 * @Author yzchao
 * @Date 2022/11/14 21:25
 * @Version V1.0
 */
@Configuration
public class SeckillOrderRabbitConfig {


    //创建交换机
    @Bean("seckillOrderExchange")
    public Exchange seckillOrderExchange(){
        return ExchangeBuilder.fanoutExchange("seckill_order_exchange").build();
    }


    //创建队列
    @Bean("seckillOrderQueue")
    public Queue seckillOrderQueue(){
        return QueueBuilder.durable("seckill_order_queue").build();
    }


    //交换机队列绑定
    @Bean
    public Binding seckillOrderBinding(@Qualifier("seckillOrderQueue") Queue seckillOrderQueue,
                                       @Qualifier("seckillOrderExchange") Exchange seckillOrderExchange){
        return BindingBuilder.bind(seckillOrderQueue).to(seckillOrderExchange).with("seckill.order.add").noargs();
    }


}
