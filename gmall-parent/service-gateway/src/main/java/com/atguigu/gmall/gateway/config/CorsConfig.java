package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @ClassName CorsConfig
 * @Description 网关跨域配置类
 * @Author yzchao
 * @Date 2022/10/24 19:04
 * @Version V1.0
 */
@Configuration
public class CorsConfig {


    @Bean
    public CorsWebFilter corsWebFilter() {
        // cors跨域配置对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true); //设置是否从服务器获取cookie
        corsConfiguration.addAllowedOrigin("*"); //设置允许访问的IP地址为任意
        corsConfiguration.addAllowedMethod("*"); //设置任何请求方法 都可请求网关
        corsConfiguration.addAllowedHeader("*"); //所有请求头信息

        //配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", corsConfiguration);
        //cors过滤器对象
        return new CorsWebFilter(configurationSource);
    }
}
