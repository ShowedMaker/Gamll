package com.atguigu.gmall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName SeckillApplication
 * @Description 秒杀微服务启动类
 * @Author yzchao
 * @Date 2022/11/14 16:57
 * @Version V1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableScheduling
public class SeckillApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SeckillApplication.class, args);
    }
}
