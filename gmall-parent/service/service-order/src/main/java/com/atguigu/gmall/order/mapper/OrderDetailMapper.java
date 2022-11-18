package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @InterfaceName OrderDetailMapper
 * @Description 订单详情表的映射
 * @Author yzchao
 * @Date 2022/11/9 19:11
 * @Version V1.0
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}
