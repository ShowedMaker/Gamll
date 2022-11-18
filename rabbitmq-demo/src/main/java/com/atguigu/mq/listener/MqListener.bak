package com.atguigu.mq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @ClassName MqListener
 * @Description
 * @Author yzchao
 * @Date 2022/11/10 15:14
 * @Version V1.0
 */
@Component
public class MqListener {

    @RabbitListener(queues = "myQueue")
    public void getMessage(Channel channel, Message message){

        byte[] body = message.getBody();
            System.out.println("消费者收到的消息为：" + new String(body));

            //获取消息的属性
            MessageProperties messageProperties = message.getMessageProperties();
            //获取消息的编号
            long deliveryTag = messageProperties.getDeliveryTag();
            System.out.println("消费者收到的消息编号为：" + deliveryTag);

        try {
            int i = 1/0;
            //确认消息
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {

            //直接消费失败
            System.out.println("消费者消费失败！");
            try {
                Boolean redelivered = messageProperties.getRedelivered();
                if (redelivered){
                    //拒绝过就是第二次消费
                    channel.basicReject(deliveryTag,false);
                }else{
                    channel.basicReject(deliveryTag,true);
                }
            } catch (IOException ex) {
                System.out.println("消费者拒绝消费 失败，没拒绝成功");
            }
        }
    }


}
