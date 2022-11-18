package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @InterfaceName OrderInfoService
 * @Description 订单微服务的接口
 * @Author yzchao
 * @Date 2022/11/9 19:07
 * @Version V1.0
 */

public interface OrderInfoService {

    /**
     * @Description 新增订单
     * @Date 16:30 2022/11/13
     * @Param [orderInfo]
     * @return void
     */
    void addOrder(OrderInfo orderInfo);

/**
 * @Description 取消订单 消息触发没有http请求 人触发是有的
 * 1.主动  2.超时   超时消息触发是不会有令牌的 人是会有的  超时触发ThreadLocal是没有用户名的 人是有的
 * 不用传状态 
 * @Date 16:34 2022/11/13
 * @Param [orderId]
 * @return void
 */
    void cancelOrder(Long orderId);
}
