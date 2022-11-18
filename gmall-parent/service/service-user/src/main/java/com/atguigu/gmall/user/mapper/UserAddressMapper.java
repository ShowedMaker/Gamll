package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.model.user.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @InterfaceName UserAddressMapper
 * @Description useraddress表的映射
 * @Author yzchao
 * @Date 2022/11/9 15:48
 * @Version V1.0
 */

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

}
