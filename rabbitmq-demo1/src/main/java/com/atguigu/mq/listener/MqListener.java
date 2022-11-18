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
 * @Date 2022/11/11 18:13
 * @Version V1.0
 */
@Component
public class MqListener {




    /**
     * @Description 监听队列
     * @Date 18:15 2022/11/11
     * @Param [channel, message]
     * @return void
     */
    @RabbitListener(queues = "direct_0509java_queue")
    public void getMessage(Channel channel, Message message){
        //获取消息的所有属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息编号
        long deliveryTag = messageProperties.getDeliveryTag();
        System.out.println("消费者接收到的消息编号为：" + deliveryTag);
        byte[] body = message.getBody();
        System.out.println("消费者接收到的消息内容为：" + new String(body));


        try {
            //确认消息
//            int i = 1/0;
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            System.out.println("消费者接受消息失败：" + deliveryTag);
            try {
                //第一次进来
                if(!messageProperties.getRedelivered()){
                    //没有被消费国
                    channel.basicReject(deliveryTag,true);
                }else{
                    //第二次进来
                    //被消费国
                    channel.basicReject(deliveryTag,false);
                    System.out.println("两次都失败");
                }
            } catch (Exception ex) {
                System.out.println("消费者拒绝消费失败：" + deliveryTag);
                ex.printStackTrace();
            }

        }


    }





}
