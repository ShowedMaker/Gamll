package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * @InterfaceName LoginService
 * @Description 自定义用户登录的服务接口
 * @Author yzchao
 * @Date 2022/11/7 19:32
 * @Version V1.0
 */

public interface LoginService {
    
    /**
     * @return java.lang.String
     * @Description 用户登录
     * @Date 19:36 2022/11/7
     * @Param [username, password]
     */
    public AuthToken login(String username, String password);
}
