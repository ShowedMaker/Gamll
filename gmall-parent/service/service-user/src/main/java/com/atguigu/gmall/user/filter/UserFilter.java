package com.atguigu.gmall.user.filter;

import com.atguigu.gmall.user.util.TokenUtil;
import com.atguigu.gmall.user.util.UserThreadLocalUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName CartFilter
 * @Description 用户微服务的过滤器
 * @Author yzchao
 * @Date 2022/11/8 22:23
 * @Version V1.0
 */
@WebFilter(filterName = "userFilter",urlPatterns = "/*")
@Order(1)
public class UserFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //获取请求头中的参数
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取令牌
        String token = request.getHeader("Authorization").replace("bearer ", "");

        //解析令牌中的载荷中的数据
        Map<String, String> map = TokenUtil.dcodeToken(token);
        //载荷存储 获取用户名
        if(null != map){
            String username = map.get("username");
            //存储用户名
            UserThreadLocalUtil.set(username);
        }

        //放行
        filterChain.doFilter(servletRequest,servletResponse);

    }
}
