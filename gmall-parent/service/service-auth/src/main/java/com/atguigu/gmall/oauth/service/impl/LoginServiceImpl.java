package com.atguigu.gmall.oauth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.oauth.service.LoginService;

import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Base64;


/**
 * @ClassName LoginServiceImpl
 * @Description 自定义用户登录的服务接口
 * @Author yzchao
 * @Date 2022/11/7 19:35
 * @Version V1.0
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private LoadBalancerClient loadBalancerClient;

    /**
     * @param username
     * @param password
     * @return java.lang.String
     * @Description 用户登录
     * @Date 19:35 2022/11/7
     * @Param [username, password]
     */
    @Override
    public AuthToken login(String username, String password) {
        //参数校验
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        ServiceInstance instance = loadBalancerClient.choose("service-oauth");
        URI uri = instance.getUri();
        //像固定的地址发送post请求 5个参数
        System.out.println("uri = " + uri);
        String url = uri.toString() + "/oauth/token";
        //HttpEntity:请求参数  responeType 返回结果类型
        //请求参数初始化 protected new不了对象  找public的参数为 （T body ，MultiValueMap headers）
        //请求头初始化
        MultiValueMap<String,String> headers = new HttpHeaders();
        //请求体初始化
        MultiValueMap<String,String> body = new HttpHeaders();
        body.set("grant_type","password");
        body.set("username",username);
        body.set("password",password);
        headers.add("Authorization",getHeadParam());
        HttpEntity httpEntity = new HttpEntity(body,headers);
        ResponseEntity<JSONObject> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);
        JSONObject resultBody = exchange.getBody();
        String accessToken = (String) resultBody.get("access_token");
        String refreshToken = (String) resultBody.get("refresh_token");
        String jti = (String) resultBody.get("jti");
        //返回结果初始化
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(accessToken);
        authToken.setRefreshToken(refreshToken);
        authToken.setJti(jti);
        //获取令牌信息返回
        return authToken;
    }
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    //拼接请求头中的参数 Basic + 空格 + new String(base64加密（客户端ID + 冒号 + 客户端密钥）)
    private String getHeadParam() {
        String a = clientId + ":" + clientSecret;
        byte[] encode = Base64.getEncoder().encode(a.getBytes());
        String result = "Basic " + new String(encode);
        return result;
    }
}
