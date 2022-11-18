package com.atguigu.gmall.product.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ProductRabbitConfig
 * @Description 商品上架下架和es数据同步的消息队列配置
 * @Author yzchao
 * @Date 2022/11/11 21:05
 * @Version V1.0
 */

@Configuration
public class ProductRabbitConfig{

    /**
     * @Description 上下架的交换机
     * @Date 21:11 2022/11/11
     * @Param []
     * @return org.springframework.amqp.core.Exchange
     */
    @Bean("product_exchange")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange("product_exchange").build();
    }

    /**
     * @Description 上架消息队列
     * @Date 21:12 2022/11/11
     * @Param []
     * @return org.springframework.amqp.core.Queue
     */
    @Bean("sku_upper_queue")
    public Queue skuUpperQueue(){
        return QueueBuilder.durable("sku_upper_queue").build();
    }

    /**
     * @Description 下架消息队列
     * @Date 21:13 2022/11/11
     * @Param []
     * @return org.springframework.amqp.core.Queue
     */
    @Bean("sku_down_queue")
    public Queue skuDownQueue(){
        return QueueBuilder.durable("sku_down_queue").build();
    }


    /**
     * @Description 上架队列绑定
     * @Date 21:19 2022/11/11
     * @Param [exchange, skuUpperQueue]
     * @return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding upperBinding(@Qualifier("product_exchange") Exchange exchange,
                                @Qualifier("sku_upper_queue") Queue skuUpperQueue){
        return BindingBuilder.bind(skuUpperQueue).to(exchange).with("sku.upper").noargs();
    }

    /**
     * @Description 下架队列绑定
     * @Date 21:20 2022/11/11
     * @Param [exchange, skuDownQueue]
     * @return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding downBinding(@Qualifier("product_exchange") Exchange exchange,
                                @Qualifier("sku_down_queue") Queue skuDownQueue){
        return BindingBuilder.bind(skuDownQueue).to(exchange).with("sku.down").noargs();
    }

}
