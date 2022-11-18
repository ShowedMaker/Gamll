package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName LoginController
 * @Description 自定义用户登录模块
 * @Author yzchao
 * @Date 2022/11/7 19:28
 * @Version V1.0
 */

@RestController
@RequestMapping("/user/login")
public class LoginController {

    @Resource
    private LoginService loginService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @Description 正常登录要用post请求 为了演示 用get
     * @Date 19:30 2022/11/7
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping()
    public Result login(String username, String password, HttpServletRequest request) {

        //登录令牌获取
        AuthToken authToken = loginService.login(username, password);

        //将登录令牌和IP绑定到一起 获取用户IP地址
        String ipAddress = IpUtil.getIpAddress(request);
        String accessToken = authToken.getAccessToken();
        //将登录令牌和IP绑定关系保存到redis中去
        stringRedisTemplate.opsForValue().set(ipAddress,accessToken);

        return Result.ok(authToken);
    }

}
