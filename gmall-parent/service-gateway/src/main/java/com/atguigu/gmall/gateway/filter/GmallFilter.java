package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.util.IpUtil;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @ClassName GmallFilter
 * @Description 全局过滤器
 * @Author yzchao
 * @Date 2022/11/8 19:38
 * @Version V1.0
 */

@Component
public class GmallFilter implements GlobalFilter, Ordered {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @Description 自定义过滤逻辑
     * @Date 19:39 2022/11/8
     * @Param [exchange, chain]
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //从请求参数中获取token
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String token = request.getQueryParams().getFirst("token");
        if(StringUtils.isEmpty(token)){
            //从请求头中获取token
            token = request.getHeaders().getFirst("token");
            if(StringUtils.isEmpty(token)){
                //从cookie中获取
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if(cookies != null){
                    HttpCookie cookieToken = cookies.getFirst("token");
                     if(cookieToken != null){
                         token = cookieToken.getValue();
                     }
                }

            }
        }

       //如果没有令牌  请求参数 请求头 cookie中都没有  就拒绝访问
        if (StringUtils.isEmpty(token)) {
            //设置拒绝响应张太麻
            response.setStatusCode(HttpStatus.BAD_GATEWAY);
            //拒绝房屋内
           return response.setComplete();
        }



        //携带了令牌token 检验是否被盗用
        String gatewayIpAddress = IpUtil.getGatewayIpAddress(request);
        String redisToken = stringRedisTemplate.opsForValue().get(gatewayIpAddress);
            //没有携带token拒绝掉
            if(StringUtils.isEmpty(redisToken)){
                //设置拒绝响应张太麻
                response.setStatusCode(HttpStatus.BAD_GATEWAY);
                //拒绝房屋内
                return response.setComplete();
            }

        //判断是否与redis中的一致
        if(!token.equals(redisToken)){
            //设置拒绝响应张太麻
            response.setStatusCode(HttpStatus.FORBIDDEN);
            //拒绝房屋内
            return response.setComplete();
        }

        //跑到这  肯定有token  将token已固定的格式(Authorization bearer)放到请求头中
        request.mutate().header("Authorization","bearer " + token);
        return chain.filter(exchange);
    }









    /**
     * @Description 全局过滤器执行顺序
     * @Date 19:39 2022/11/8
     * @Param []
     * @return int
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
