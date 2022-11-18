package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @InterfaceName OrderInfoMapper
 * @Description 订单表的映射
 * @Author yzchao
 * @Date 2022/11/9 19:06
 * @Version V1.0
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
