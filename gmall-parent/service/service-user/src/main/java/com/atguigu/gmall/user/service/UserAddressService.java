package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @InterfaceName UserAddressService
 * @Description 用户收获地址接口
 * @Author yzchao
 * @Date 2022/11/9 15:50
 * @Version V1.0
 */

public interface UserAddressService {

    /**
     * @Description 查询用户收货地址 可能多个
     * @Date 15:52 2022/11/9
     * @Param []
     * @return java.util.List<com.atguigu.gmall.model.user.UserAddress>
     */
    List<UserAddress> getUserAddressList();

}
