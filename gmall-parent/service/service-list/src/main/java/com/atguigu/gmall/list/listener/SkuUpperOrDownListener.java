package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.GoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @ClassName SkuUpperOrDownListener
 * @Description 监听商品的上架下架的消费者监听类 同步消息
 * @Author yzchao
 * @Date 2022/11/11 23:00
 * @Version V1.0
 */
@Component
@Log4j2
public class SkuUpperOrDownListener {

    @Resource
    private GoodsService goodsService;
    /**
     * @Description 监听商品上架消息
     * @Date 23:01 2022/11/11
     * @Param [message, channel]
     * @return void
     */
    @RabbitListener(queues = "sku_upper_queue")
    public void SkuUpper(Message message, Channel channel){
        MessageProperties messageProperties = message.getMessageProperties();
        String body = new String(message.getBody());
        Long skuId = Long.parseLong(body);
        Long deliveryTag = messageProperties.getDeliveryTag();


        try {

            goodsService.goodsFromDBToEs(skuId);
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {

            try {
                if(messageProperties.getRedelivered()){
                    e.printStackTrace();
                    log.error("商品上架失败,商品的id为: " + skuId);
                   channel.basicReject(deliveryTag,false);
                }else{
                    channel.basicReject(deliveryTag,true);
                }
            } catch (Exception ex) {
//                redisTemplate.opsForValue().set("reject_fail","商品上架两次拒绝消费失败" + skuId+",失败的原因是：" + ex.getMessage());
                log.error("商品上架拒绝消息失败, 失败的商品id为:" + skuId + ",失败的原因为:" + ex.getMessage());
            }
        }
    }




    /**
     * @Description 监听商品下架消息
     * @Date 23:01 2022/11/11
     * @Param [message, channel]
     * @return void
     */
    @RabbitListener(queues = "sku_down_queue")
    public void SkuDown(Message message, Channel channel){
        MessageProperties messageProperties = message.getMessageProperties();
        String body = new String(message.getBody());
        Long skuId = Long.parseLong(body);
        Long deliveryTag = messageProperties.getDeliveryTag();


        try {
            goodsService.goodsRemoveFromEs(skuId);
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {

            try {
                if(messageProperties.getRedelivered()){
                    log.error("商品下架失败,商品的id为: " + skuId);
                    channel.basicReject(deliveryTag,false);
                }else{
                    e.printStackTrace();
                    channel.basicReject(deliveryTag,true);

                }
            } catch (Exception ex) {
//                redisTemplate.opsForValue().set("reject_fail","商品下架两次拒绝消费失败" + skuId+",失败的原因是：" + ex.getMessage());
                log.error("商品下架拒绝消息失败, 失败的商品id为:" + skuId + ",失败的原因为:" + ex.getMessage());
            }
        }
    }




}
