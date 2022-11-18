package com.atguigu.gmall.payment.service;

import java.util.Map;

/**
 * @InterfaceName ZfbPayService
 * @Description 支付宝付款的接口类
 * @Author yzchao
 * @Date 2022/11/15 13:49
 * @Version V1.0
 */

public interface ZfbPayService {

    /**
     * @Description 获取支付宝支付的页面地址
     * @Date 15:00 2022/11/15
     * @Param [orderId, money, desc]
     * @return java.lang.String
     */
    public String getPayPage(String orderId, String money, String desc);


    /**
     * @Description 查询指定订单的支付结果
     * @Date 22:34 2022/11/13
     * @Param [orderId]
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public String getPayResult(String orderId);



}
