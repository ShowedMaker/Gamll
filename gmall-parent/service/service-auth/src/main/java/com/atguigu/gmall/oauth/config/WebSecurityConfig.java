package com.atguigu.gmall.oauth.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//放行配置 只要项目引入了oauth2.0坐标 只有有令牌才能访问
@Configuration
@EnableWebSecurity
@Order(-1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //忽略安全拦截的URL 放行登录登出
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/user/login",
                "/user/logout");
    }
    //创建授权管理认证对象
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }
    //采用BCryptPasswordEncoder对密码进行编码 数据库密码的加密  数据库用什么方式的加密这里也要用一样的加密方式
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //http过滤验证
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic()        //启用Http基本身份验证
                .and()
                .formLogin()       //启用表单身份验证
                .and()
                .authorizeRequests()    //限制基于Request请求访问
                .anyRequest()
                .authenticated();       //其他请求都需要经过验证
    }
}
