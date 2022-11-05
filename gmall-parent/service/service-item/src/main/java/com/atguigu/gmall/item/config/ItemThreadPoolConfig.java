package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ItemThreadPoolConfig
 * @Description 商品详情微服务线程池配置类
 * @Author yzchao
 * @Date 2022/11/2 0:01
 * @Version V1.0
 */
@Configuration
public class ItemThreadPoolConfig {


    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        //7个参数
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(12, 24, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.execute(()->{
            System.out.println("使用线程池的线程执行任务");
        });
        return threadPoolExecutor;
    }

}
