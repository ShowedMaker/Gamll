package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName OrderDelayRabbitConfig
 * @Description 订单超时消息的配置类
 * @Author yzchao
 * @Date 2022/11/13 14:48
 * @Version V1.0
 */

@Configuration
public class OrderDelayRabbitConfig {

    /**
     * 创建正常的交换机
     */
    @Bean("orderNormalExchange")
    public Exchange orderNormalExchange(){
        return ExchangeBuilder.directExchange("order_normal_exchange").build();
    }


    /**
     * 死信队列
     */
    @Bean("orderDeadQueue")
    public Queue orderDeadQueue(){
        return QueueBuilder
                .durable("order_dead_queue")
                .withArgument("x-dead-letter-exchange", "order_dead_exchange")
                .withArgument("x-dead-letter-routing-key", "order.dead")
                .build();
    }

    /**
     * 正常交换机和死信队列绑定
     * @param orderNormalExchange
     * @param orderDeadQueue
     * @return
     */
    @Bean
    public Binding normalBinding(@Qualifier("orderNormalExchange") Exchange orderNormalExchange,
                                @Qualifier("orderDeadQueue") Queue orderDeadQueue){
        return BindingBuilder.bind(orderDeadQueue).to(orderNormalExchange).with("order.normal").noargs();
    }


    /**
     * 创建死信的交换机
     */
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange(){
        return ExchangeBuilder.directExchange("order_dead_exchange").build();
    }

    /**
     * 正常队列
     * @return
     */
    @Bean("orderNormalQueue")
    public Queue orderNormalQueue(){
        return QueueBuilder.durable("order_normal_queue").build();
    }

    /**
     * 死信交换机和正常队列绑定
     * @param orderDeadExchange
     * @param orderNormalQueue
     * @return
     */
    @Bean
    public Binding deadBinding(@Qualifier("orderDeadExchange") Exchange orderDeadExchange,
                               @Qualifier("orderNormalQueue") Queue orderNormalQueue){
        return BindingBuilder.bind(orderNormalQueue).to(orderDeadExchange).with("order.dead").noargs();
    }
}
