package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName UserAddressController
 * @Description 用户收货地址控制层
 * @Author yzchao
 * @Date 2022/11/9 15:57
 * @Version V1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @GetMapping("/getUserAddress")
    public Result getUserAddressList(){
        List<UserAddress> userAddressList =
                userAddressService.getUserAddressList();
        return Result.ok(userAddressList);
    }



}
