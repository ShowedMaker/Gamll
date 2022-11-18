package com.atguigu.gmall.seckill.config;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPoolConfig
 * @Description 秒杀服务的线程池配置类
 * @Author yzchao
 * @Date 2022/11/14 20:15
 * @Version V1.0
 */
@Configuration
public class ThreadPoolConfig {


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(24,
                24,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000));
    }




}
