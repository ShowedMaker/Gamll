package com.atguigu.gmall.seckill.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName ReturnAndConfirmCallback
 * @Description
 * @Author yzchao
 * @Date 2022/11/10 15:31
 * @Version V1.0
 */

@Component
public class ReturnAndConfirmCallback implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback{


    /**
     * @Description
     * @Date 15:32 2022/11/10
     * @Param [correlationData, b, s]
     * @return void
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {

        /**
         * @Description 没有抵达交换机 触发
         * @Date 15:50 2022/11/10
         * @Param [correlationData, b, s]
         * @return void
         */
        if(!b){
            System.out.println("为true, 值为：" + s);
            //记录日志
        }
    }

    
    /**
     * @Description 消息抵达交换机 不抵达队列触发
     * @Date 15:48 2022/11/10
     * @Param [message, i, s, s1, s2]
     * @return void
     */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        byte[] body = message.getBody();
        System.out.println("return模式收到的消息为：" + new String(body));
        System.out.println("return模式错误码为：" + new String(body));
        System.out.println("return模式错误内容为：" + new String(body));
        System.out.println("return模式交换机名称为：" + new String(body));
        System.out.println("return模式路由键为：" + new String(body));
        //记录日志到存储地方
    }
}
