package com.atguigu.gmall.payment.constant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName WxPayConstant
 * @Description 微信支付的常量类
 * @Author yzchao
 * @Date 2022/11/13 21:23
 * @Version V1.0
 */
@Component
public class WxPayConstantUtils implements InitializingBean {

    @Value("${weixin.pay.requestUrl}")
    private String requestUrl;
    @Value("${weixin.pay.appid}")
    private String appId;
    @Value("${weixin.pay.partner}")
    private String partner;
    @Value("${weixin.pay.partnerKey}")
    private String partnerKey;
    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;
    @Value("${weixin.pay.payResultUrl}")
    private String payResultUrl;

    public static String REQUEST_URL;
    public static String PAY_RESULT_URL;
    public static String APPID;
    public static String PARTNER_KEY;
    public static String NOTIFY_URL;
    public static String PARTNER;


    @Override
    public void afterPropertiesSet() throws Exception {
        REQUEST_URL = requestUrl;
        PAY_RESULT_URL = payResultUrl;
        APPID = appId;
        PARTNER_KEY = partnerKey;
        NOTIFY_URL = notifyUrl;
        PARTNER = partner;
    }
}
