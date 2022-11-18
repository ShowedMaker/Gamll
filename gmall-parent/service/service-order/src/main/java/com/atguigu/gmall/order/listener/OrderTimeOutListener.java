package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName OrderTimeOutListener
 * @Description 订单超时的监听
 * @Author yzchao
 * @Date 2022/11/13 16:19
 * @Version V1.0
 */
@Component
@Log4j2
public class OrderTimeOutListener {


    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * @Description 监听订单超时消息
     * @Date 23:01 2022/11/11
     * @Param [message, channel]
     * @return void
     */
    @RabbitListener(queues = "order_normal_queue")
    public void OrderTimeOut(Message message, Channel channel){
        MessageProperties messageProperties = message.getMessageProperties();
        String body = new String(message.getBody());
        Long orderId = Long.parseLong(body);
        Long deliveryTag = messageProperties.getDeliveryTag();


        try {
            //超时取消订单         用户一起调 会容易有并发
            orderInfoService.cancelOrder(orderId);
            System.out.println("orderId = " + orderId);
            System.out.println("收到消息的时间戳为：" + System.currentTimeMillis() + ",消息的订单编号为： " + orderId);
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {

            try {
                if(messageProperties.getRedelivered()){
                    e.printStackTrace();
                    log.error("订单超时,订单的id为: " + orderId);
                    channel.basicReject(deliveryTag,false);
                }else{
                    channel.basicReject(deliveryTag,true);
                }
            } catch (Exception ex) {
//                redisTemplate.opsForValue().set("reject_fail","商品上架两次拒绝消费失败" + skuId+",失败的原因是：" + ex.getMessage());
                log.error("订单超时拒绝消息失败, 失败的订单id为:" + orderId + ",失败的原因为:" + ex.getMessage());
            }
        }
    }
}
