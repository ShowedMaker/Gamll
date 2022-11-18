package com.atguigu.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MqConfig
 * @Description
 * @Author yzchao
 * @Date 2022/11/11 16:44
 * @Version V1.0
 */
@Configuration
public class MqConfig {


    @Bean("direct_0509java_exchange")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange("direct_0509java_exchange").build();
    }

    @Bean("direct_0509java_queue")
    public Queue queue(){
        return QueueBuilder.durable("direct_0509java_queue").build();
    }

    @Bean("queue_routeKey_exchange")
    public Binding binding(@Qualifier("direct_0509java_exchange") Exchange exchange,
                           @Qualifier("direct_0509java_queue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("queue_routeKey_exchange").noargs();
    }


}
