package com.atguigu.gmall.order.intercepter;

import com.atguigu.gmall.common.exception.GmallException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @ClassName OrderIntercepter
 * @Description 订单微服务的拦截器
             * 过滤器: Servlet组件--->进入微服务的请求
             * 拦截器: SpringMVC--->从微服务出去的请求
 * @Author yzchao
 * @Date 2022/11/9 21:13
 * @Version V1.0
 */
@Component
public class OrderIntercepter implements RequestInterceptor {

    /**
     * @Description 触发时间 feign调用触发前
     * @Date 21:15 2022/11/9
     * @Param [requestTemplate]
     * @return void
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取原始的请求对象 墙砖 ServletRequestAttributes 为RequestAttributes的实现类 里面有HttpServletRequest 和 HttpServletResponse两个属性
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            //获取原始请求对象的请求体
            HttpServletRequest request = requestAttributes.getRequest();
            //获取原始请求体的请求头中的所有的参数
            Enumeration<String> headerNames = request.getHeaderNames();
            //迭代
            while(headerNames.hasMoreElements()){
                    //获取每个请求头的参数的名字
                String everyHeaderName = headerNames.nextElement();
                    //获取值
                String header = request.getHeader(everyHeaderName);
                //存储到feign的http请求头中去
                requestTemplate.header(everyHeaderName,header);
            }
        }
    }
}
