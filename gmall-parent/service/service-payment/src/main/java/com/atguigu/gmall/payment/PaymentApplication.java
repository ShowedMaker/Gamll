package com.atguigu.gmall.payment;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.atguigu.gmall.payment.constant.ZfbPayConstantUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName PaymentApplication
 * @Description 商品支付的微服务启动类
 * @Author yzchao
 * @Date 2022/11/13 21:10
 * @Version V1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class,args);
    }

    /**
     * @Description 支付宝客户端初始化
     * @Date 16:59 2022/11/15
     * @Param []
     * @return void
     */
    @Bean
    public AlipayClient zfbInit(){
        AlipayClient alipayClient = new DefaultAlipayClient
                (ZfbPayConstantUtils.ALIPAY_URL,ZfbPayConstantUtils.APPID,ZfbPayConstantUtils.APP_PRIVATE_KEY,
                        "json","utf-8",ZfbPayConstantUtils.ALIPAY_PUBLIC_KEY,"RSA2");;
                        return alipayClient;
    }
}
