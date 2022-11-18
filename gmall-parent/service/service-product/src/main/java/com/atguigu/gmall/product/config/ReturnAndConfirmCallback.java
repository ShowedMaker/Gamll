package com.atguigu.gmall.product.config;//package com.atguigu.gmall.list.config;
//

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
//
/**
 * @ClassName ReturnAndConfirmCallback
 * @Description 商品上下架 mq可靠性投递的 return机制和confirm机制 配置类
 * @Author yzchao
 * @Date 2022/11/11 22:43
 * @Version V1.0
 */
@Component
@Log4j2
public class ReturnAndConfirmCallback implements RabbitTemplate.ReturnCallback,RabbitTemplate.ConfirmCallback {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this::confirm);
        rabbitTemplate.setReturnCallback(this::returnedMessage);
    }



    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(!b){
            System.out.println("可靠性投递confirm机制返回的是boolean值是:" + b + ",消息值为：" + s);
            //重发
            //记录日志
            log.error("商品上架失败,商品的id为: " + s);
            redisTemplate.opsForValue().set("confirm:log","没有触发confirm机制的消息内容为："+s);

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
        log.error("商品上架失败,商品的id为: " + s);
        redisTemplate.opsForValue().set("return:log","商品的上下架同步失败，商品的id为："+ new String(body)+"," +
                "上架(1)或下架(0)的动作为：" + s2);
    }
}
