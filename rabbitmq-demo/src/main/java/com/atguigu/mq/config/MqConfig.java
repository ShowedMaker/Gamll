package com.atguigu.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MqConfig
 * @Description
 * @Author yzchao
 * @Date 2022/11/10 14:13
 * @Version V1.0
 */
@Configuration
public class MqConfig {


    /**
     * @Description 定义交换机 交给spring管理
     * @Date 14:15 2022/11/10
     * @Param []
     * @return org.springframework.amqp.core.Exchange
     */
    @Bean
    public Exchange myExchange(){
        return ExchangeBuilder.directExchange("myExchange").build();
    }


    /**
     * @Description 定义队列 交给spring管理
     * @Date 14:16 2022/11/10
     * @Param []
     * @return org.springframework.amqp.core.Queue
     */
    @Bean
    public Queue myQueue(){
        return QueueBuilder.durable("myQueue").build();
    }


    @Bean
    public Binding myBinding(@Qualifier("myExchange") Exchange myExchange,
                             @Qualifier("myQueue") Queue myQueue  ){
        return BindingBuilder.bind(myQueue).to(myExchange).with("myQueue_routeKey_myExchange").noargs();
    }




}
