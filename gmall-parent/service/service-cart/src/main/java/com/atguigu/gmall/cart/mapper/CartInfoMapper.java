package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @InterfaceName CartInfoMapper
 * @Description 购物车mapper映射
 * @Author yzchao
 * @Date 2022/11/8 15:15
 * @Version V1.0
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {

    @Update("update cart_info set sku_num = #{number} where id = #{id} and user_id = #{username}")
    public int updateCartNum(Long id, Integer number, String username);

    @Update("update cart_info set is_checked = #{status} where user_id = #{username} and id = #{cartId}")
    public int updateCheck(Long cartId,Short status,String username);

    @Update("update cart_info set is_checked = #{status} where user_id = #{username}")
    public int updateCheckAll(Short status,String username);
}
