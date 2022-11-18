package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.payment.constant.ZfbPayConstantUtils;
import com.atguigu.gmall.payment.service.ZfbPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @ClassName ZfbPayServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/15 15:20
 * @Version V1.0
 */
@Service
public class ZfbPayServiceImpl implements ZfbPayService {

    @Autowired
    private AlipayClient alipayClient;
    /**
     * @param orderId
     * @param money
     * @param desc
     * @return java.lang.String
     * @Description 获取支付宝支付的页面地址
     * @Date 15:00 2022/11/15
     * @Param [orderId, money, desc]
     */
    @Override
    public String getPayPage(String orderId, String money, String desc) {

        //请求对象初始化
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //用户付完款之后去哪里通知商家 用户已经付完款了
        request.setNotifyUrl(ZfbPayConstantUtils.NOTIFY_PAYMENT_URL);
        //用户付完款以后 跳回到那个页面
        request.setReturnUrl(ZfbPayConstantUtils.RETURN_PAYMENT_URL);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "java050901010101"+ orderId);
        bizContent.put("total_amount", money);
        bizContent.put("subject", desc);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //设置请求参数
        //bizContent.put("time_expire", "2022-08-01 22:00:00");

        //// 商品明细信息，按需传入
        //JSONArray goodsDetail = new JSONArray();
        //JSONObject goods1 = new JSONObject();
        //goods1.put("goods_id", "goodsNo1");
        //goods1.put("goods_name", "子商品1");
        //goods1.put("quantity", 1);
        //goods1.put("price", 0.01);
        //goodsDetail.add(goods1);
        //bizContent.put("goods_detail", goodsDetail);

        //// 扩展信息，按需传入
        //JSONObject extendParams = new JSONObject();
        //extendParams.put("sys_service_provider_id", "2088511833207846");
        //bizContent.put("extend_params", extendParams);

        //发起请求
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
               return response.getBody();
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param orderId
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Description 查询指定订单的支付结果
     * @Date 22:34 2022/11/13
     * @Param [orderId]
     */
    @Override
    public String getPayResult(String orderId) {

        if(StringUtils.isEmpty(orderId)){
            return null;
        }

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "java050901010101"+ orderId);
        //bizContent.put("trade_no", "2014112611001004680073956707");
        request.setBizContent(bizContent.toString());

        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                String body = response.getBody();
                return body;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
