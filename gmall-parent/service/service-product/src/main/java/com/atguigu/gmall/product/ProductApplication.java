package com.atguigu.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName ProductApplication
 * @Description 商品详情管理微服务的启动类
 * @Author yzchao
 * @Date 2022/10/20 10:41
 * @Version V1.0
 */
@SpringBootApplication
@EnableDiscoveryClient //注册到nacos注册中心
@ComponentScan("com.atguigu.gmall") //扫描的目的是扫描service-util中的加了@Configuration的config,constant,handler,cache有注解标注的 这些都要用service微服务都要用，所以扫描整个包
@EnableFeignClients(basePackages = {"com.atguigu.gmall.list"})
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}
