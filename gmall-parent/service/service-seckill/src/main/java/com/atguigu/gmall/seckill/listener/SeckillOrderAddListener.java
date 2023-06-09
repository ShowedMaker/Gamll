package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName SeckillOrderAddListener
 * @Description 监听秒杀下单消息的消费者
 * @Author yzchao
 * @Date 2022/11/14 22:19
 * @Version V1.0
 */
@Component
@Log4j2
public class SeckillOrderAddListener {


    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * @Description 异步下单
     * @Date 22:20 2022/11/14
     * @Param [message, channel]
     * @return void
     */
    @RabbitListener(queues = "seckill_order_queue")
    public void seckillOrderAdd(Message message, Channel channel){
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        Long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息的内容
        String s = new String(message.getBody());
        try {
            //秒杀下单
            seckillOrderService.listenerAddSeckillOrder(s);
            System.out.println("--==============================" + s);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //只尝试一次
                channel.basicReject(deliveryTag, false);
            }catch (Exception e1){
                log.error("秒杀下单失败, 排队的信息为:" + s + ",失败的原因为:" + e1.getMessage());
            }
        }
    }
}
