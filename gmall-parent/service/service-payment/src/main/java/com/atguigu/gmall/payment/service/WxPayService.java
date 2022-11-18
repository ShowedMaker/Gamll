package com.atguigu.gmall.payment.service;

import java.util.Map;

/**
 * @InterfaceName WxPayService
 * @Description 微信支付的接口
 * @Author yzchao
 * @Date 2022/11/13 21:13
 * @Version V1.0
 */

public interface WxPayService {

    /**
     * @return void
     * @Description 获取微信支付二维码地址
     * @Date 21:18 2022/11/13
     * @Param [orderId, money, desc]
     */
    public Map<String, String> getPayCode(String orderId, String money, String desc);

    /**
     * @Description 查询指定订单的支付结果
     * @Date 22:34 2022/11/13
     * @Param [orderId]
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public Map<String, String> getPayResult(String orderId);
}
