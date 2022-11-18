package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.ZfbPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName ZfbPayController
 * @Description 支付宝支付的控制层
 * @Author yzchao
 * @Date 2022/11/15 16:15
 * @Version V1.0
 */
@RestController
@RequestMapping("/zfb/pay")
public class ZfbPayController {

    @Resource
    private ZfbPayService zfbPayService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * @Description 请求支付宝的付款页面
     * @Date 16:22 2022/11/15
     * @Param [orderId, money, desc]
     * @return java.lang.String
     */
    @GetMapping("/getPayCodeUrl")
    public String getPayCodeUrl(String orderId, String money, String desc){
        return zfbPayService.getPayPage(orderId,money,desc);
    }


    @GetMapping("/getPayResult")
    public String getPayResult(String orderId){
        return zfbPayService.getPayResult(orderId);
    }

    /**
     * @Description 同步回调  支付包支付完成，回到商城
     * @Date 18:04 2022/11/15
     * @Param []
     * @return java.lang.String
     */
    @RequestMapping("/callback/return")
    public String callBackReturn(@RequestParam Map<String,String> returnMap){
        String s = JSONObject.toJSONString(returnMap);
        return "同步回调 支付包支付完成，回到商城";
    }


    /**
     * @Description 异步回调 用户支付完 支付宝到哪里告诉商城支付结果
     * @Date 18:08 2022/11/15
     * @Param [map]
     * @return java.lang.String
     */
    @RequestMapping("/callback/notify")
    public String callBackNotify(@RequestParam Map<String,String> callbackMap){
        String result = "{\"gmt_create\":\"2022-11-14 09:13:34\",\"charset\":\"utf-8\"," +
                "\"gmt_payment\":\"2022-11-14 09:13:38\",\"notify_time\":\"2022-11-14 09:13:38\"," +
                "\"subject\":\"梅旺旺的大玩具4565\",\"sign\":\"A3w3jXIf3GDH8uqbfqqEbmK/o1vGLF/VYnej1hEiJhJs8tg19OSJM7fo6BFbMxT0jRH9L5gm+ppiCicNQZy1338lrhKZuaStiWAWw3kVZP1MVM3LRX9HUZcnsAJs984vcpeDcB7aIxaCJYDn6zd2nBq95CVSu6L2g7O0aS9ZuEzN80A9juvE10B8y4ZH86iCv8A71HinxW5e96ski/fj8QvUWAyG6/RRZnGxGa+HWj3dDJ++oARWf2mE5hLZOlhwW3+oUr2rhHIbM3Lv0YOk168eoY+Q7b99opoYWIofjdAcd77+/fw/xQBSmyKtvrHPpLHe7285nhc7akkuSoHsBQ==\"," +
                "\"buyer_id\":\"2088512600092751\",\"invoice_amount\":\"0.01\"," +
                "\"version\":\"1.0\",\"notify_id\":\"2022111401222091338092751417015454\"," +
                "\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\"," +
                "\"notify_type\":\"trade_status_sync\",\"out_trade_no\":\"19\",\"total_amount\":\"0.01\"," +
                "\"trade_status\":\"TRADE_SUCCESS\",\"trade_no\":\"2022111422001492751406232110\",\"auth_app_id\":\"2021001163617452\"," +
                "\"receipt_amount\":\"0.01\",\"point_amount\":\"0.00\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"2021001163617452\",\"" +
                "sign_type\":\"RSA2\",\"seller_id\":\"2088831489324244\"}";

        rabbitTemplate.convertAndSend("pay_exchange", "pay.order" , result);
        return "异步回调 用户支付完 支付宝到哪里告诉商城支付结果";
    }


}
