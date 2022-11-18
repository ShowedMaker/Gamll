package com.atguigu.gmall.oauth.mapper;

import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * @InterfaceName UserInfoMapper
 * @Description userInfo接口
 * @Author yzchao
 * @Date 2022/11/7 20:41
 * @Version V1.0
 */

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
