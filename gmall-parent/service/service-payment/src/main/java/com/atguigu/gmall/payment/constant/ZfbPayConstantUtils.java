package com.atguigu.gmall.payment.constant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName WxPayConstant
 * @Description 支付宝支付的常量类
 * @Author yzchao
 * @Date 2022/11/13 21:23
 * @Version V1.0
 */
@Component
public class ZfbPayConstantUtils implements InitializingBean {

    @Value("${alipay_url}")
    private String alipayUrl;
    @Value("${app_id}")
    private String appId;
    @Value("${app_private_key}")
    private String appPrivateKey;
    @Value("${alipay_public_key}")
    private String alipayPublicKey;
    @Value("${return_payment_url}")
    private String returnPaymentUrl;
    @Value("${notify_payment_url}")
    private String notifyPaymentUrl;
    @Value("${return_order_url}")
    private String returnOrderUrl;

    public static String ALIPAY_URL;
    public static String APP_PRIVATE_KEY;
    public static String APPID;
    public static String ALIPAY_PUBLIC_KEY;
    public static String RETURN_PAYMENT_URL;
    public static String NOTIFY_PAYMENT_URL;
    public static String RETURN_ORDER_URL;


    @Override
    public void afterPropertiesSet() throws Exception {
        ALIPAY_URL = alipayUrl;
        APPID = appId;
        APP_PRIVATE_KEY = appPrivateKey;
        ALIPAY_PUBLIC_KEY = alipayPublicKey;
        RETURN_PAYMENT_URL = returnPaymentUrl;
        NOTIFY_PAYMENT_URL = notifyPaymentUrl;
        RETURN_ORDER_URL = returnOrderUrl;
    }
}
