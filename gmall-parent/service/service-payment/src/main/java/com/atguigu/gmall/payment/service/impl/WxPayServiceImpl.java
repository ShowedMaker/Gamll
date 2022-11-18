package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.payment.constant.WxPayConstantUtils;
import com.atguigu.gmall.payment.service.WxPayService;
import com.atguigu.gmall.payment.util.HttpClient;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WxPayServiceImpl
 * @Description 微信支付接口的实现类
 * @Author yzchao
 * @Date 2022/11/13 21:18
 * @Version V1.0
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    /**
     * @param orderId 订单id
     * @param money   订单金额
     * @param desc    订单描述
     * @return void
     * @Description 获取微信支付二维码地址
     * @Date 21:18 2022/11/13
     * @Param [orderId, money, desc]
     */
    @Override
    public Map<String, String> getPayCode(String orderId, String money, String desc) {
        //参数校验  内部调用 可以调用之前校验
        if(StringUtils.isEmpty(orderId) || StringUtils.isEmpty(money) || StringUtils.isEmpty(desc)){
            return null;
        }
        try {
            //获取微信同意下单接口的url
            String requestUrl = WxPayConstantUtils.REQUEST_URL;
            //像这个地址发起Post请求   包装请求参数 xml
            //用微信给的工具类 map 转 xml
            Map<String,String> map = new HashMap<>();
            map.put("appid", WxPayConstantUtils.APPID);
            map.put("mch_id", WxPayConstantUtils.PARTNER);
            map.put("nonce_str",WXPayUtil.generateNonceStr());
//        map 转为xml 同时 生成签名
//        map.put("sign",WXPayUtil.generateSignedXml(map,partnerkey));
            map.put("body",desc);
            map.put("out_trade_no","java05091111" + orderId);
            map.put("total_fee",money);
            map.put("spbill_create_ip","192.168.200.1");
            map.put("notify_url", WxPayConstantUtils.NOTIFY_URL);
            map.put("trade_type","NATIVE");
            //调用方法发送http请求
            String contentXml = sendHttpRequest(map, requestUrl);
//            //解析结果 微信返给我们的xml 转为map
//            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml);
//
//            if(resultMap.get("return_code").equals("SUCCESS")
//                    && resultMap.get("result_code").equals("SUCCESS")){
//                //返回微信给过来的二维码地址
//                return resultMap;

            //直接把结果返给订单微服务  至于成功还是失败 订单微服务自己判断
            return WXPayUtil.xmlToMap(contentXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param orderId
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Description 查询指定订单的支付结果
     * @Date 22:32 2022/11/13
     * @Param [orderId]
     */
    @Override
    public Map<String, String> getPayResult(String orderId) {
        if(StringUtils.isEmpty(orderId)){
            return null;
        }
        try {
            String payResultUrl = WxPayConstantUtils.PAY_RESULT_URL;
            Map<String,String> map = new HashMap<>();
            map.put("appid", WxPayConstantUtils.APPID);
            map.put("mch_id", WxPayConstantUtils.PARTNER);
            map.put("nonce_str",WXPayUtil.generateNonceStr());
            map.put("out_trade_no","java05091111" + orderId);
            //调用方法发送http请求
            String contentXml = sendHttpRequest(map, payResultUrl);
            return WXPayUtil.xmlToMap(contentXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //抽取方法发送http请求
    private String sendHttpRequest(Map<String,String> map,String url) {
        try {
            //将map的数据转换为xml同时生成签名
            String xmlParam = WXPayUtil.generateSignedXml(map,
                    WxPayConstantUtils.PARTNER_KEY);
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            //发出post请求 一调请求就发出去
            httpClient.post();
            //获取post请求结果
            return httpClient.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
