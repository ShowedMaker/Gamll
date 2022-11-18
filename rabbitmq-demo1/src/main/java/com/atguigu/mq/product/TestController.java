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
 * @Date 2022/11/11 16:41
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
    public String sendMessage(){

        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);


        rabbitTemplate.convertAndSend("direct_0509java_exchange",
                "queue_routeKey_exchange","2022-11-11-生产者发送消息测试");
        return "生产者发送消息成功";
    }
}
