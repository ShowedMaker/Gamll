package com.atguigu.mq.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName ReturnAndConfirmCallback
 * @Description
 * @Author yzchao
 * @Date 2022/11/11 17:38
 * @Version V1.0
 */
@Component
public class ReturnAndConfirmCallback implements RabbitTemplate.ReturnCallback,RabbitTemplate.ConfirmCallback {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(!b){
            System.out.println("confirm返回的是boolean值是false:" + b + ",消息值为：" + s);
            //重发
            //记录日志
            redisTemplate.opsForValue().set("没有触发confirm机制的消息内容为：",s);

        }
    }


    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        byte[] body = message.getBody();
        System.out.println("return机制触发的消息内容为：" + new String(body));
        System.out.println("return触发的错误码内容为:" +  i);
        System.out.println("return触发的错误内容内容为:" +  s);
        System.out.println("return触发交换机的内容为:" + s1);
        System.out.println("return触发的路由键内容为:" + s2);
        //重发
        //记录日志
        redisTemplate.opsForValue().set("触发return机制,没有进队列的消息内容为：",s);
    }
}
