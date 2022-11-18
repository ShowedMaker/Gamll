package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.atguigu.gmall.user.util.UserThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName UserAddressServiceImpl
 * @Description
 * @Author yzchao
 * @Date 2022/11/9 15:51
 * @Version V1.0
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    /**
     * @return java.util.List<com.atguigu.gmall.model.user.UserAddress>
     * @Description 查询用户收货地址 可能多个
     * @Date 15:52 2022/11/9
     * @Param []
     */
    @Override
    public List<UserAddress> getUserAddressList() {
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId,UserThreadLocalUtil.get()));
    }
}


