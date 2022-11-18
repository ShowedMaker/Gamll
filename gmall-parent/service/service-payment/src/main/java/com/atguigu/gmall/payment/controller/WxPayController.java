package com.atguigu.gmall.payment.controller;

import brave.http.HttpServerRequest;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WxPayController
 * @Description 微信支付控制层
 * @Author yzchao
 * @Date 2022/11/13 22:03
 * @Version V1.0
 */
@RestController
@RequestMapping("/wx/pay")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;

    /**
     * @Description 获取微信支付二维码地址 给订单微服务调用
     * @Date 22:07 2022/11/13
     * @Param [orderId, money, desc]
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    @GetMapping("/getPayCodeUrl")
    public Map<String,String> getPayCodeUrl(@RequestParam String orderId, //map类型 浏览器请求可以不加   feign调用请求必须加@RequestParam 否则拿不到参数
                                            @RequestParam String money,
                                            @RequestParam String desc){
        return wxPayService.getPayCode(orderId, money, desc);
    }

    /**
     * @Description 查询指定订单的支付结果
     * @Date 22:57 2022/11/13
     * @Param [orderId]
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    @GetMapping("/getPayResult")
    public Map<String,String> getPayResult(@RequestParam String orderId){
        return wxPayService.getPayResult(orderId);
    }


    /**
     * @Description 按照数据流 把相应结果应答给微信  否则微信会一直调这个接口
     * 支付失败 微信不回调你
     * @Date 23:07 2022/11/13
     * @Param [request]
     * @return Sting
     */
    @RequestMapping("/notify/callback")
    public String notifyCallBack(HttpServletRequest request){
        try {
            //获取微信传递过来的数据流  主要是将字节码转换为String字符串
            ServletInputStream is = request.getInputStream();
            //定义输出流
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //定义缓冲区
            byte[] buffer = new byte[1024];
            //定义读取的长度
            int len = 0;
            while((len = is.read(buffer)) != -1){
                os.write(buffer,0,len);
            }
            os.flush();
            //将流中字节转为String
            String xmlResult = new String(os.toByteArray());
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            //反序列化
            System.out.println(JSONObject.toJSONString(map));

            //把这个支付结果告诉订单微服务  支付微服务没有权限去改订单系统 支付结果成功失败订单微服务自己决定后面做什么



            os.close();
            is.close();

            //返回结果给微信
            Map<String,String> toWxMap = new HashMap<>();
            toWxMap.put("return_code","SUCCESS");
            toWxMap.put("return_msg","OK");
            return WXPayUtil.mapToXml(toWxMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
