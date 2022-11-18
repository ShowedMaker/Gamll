package com.atguigu.mq.product;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestController
 * @Description
 * @Author yzchao
 * @Date 2022/11/10 14:10
 * @Version V1.0
 */
@RestController
@RequestMapping("/send")
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitTemplate.ConfirmCallback confirmCallback;
    @Autowired
    private RabbitTemplate.ReturnCallback returnCallback;



    @GetMapping
    public String send(){
        //设置confirm对象的回调方法
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //设return对象的
        rabbitTemplate.setReturnCallback(returnCallback);

            rabbitTemplate.convertAndSend("myExchange","myQueue_routeKey_myExchange","mq测试发送小新");
        System.out.println("生产者消息发送成功了！！！");
        return "成功";
    }

}
