package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * @InterfaceName CartService
 * @Description 购物车接口
 * @Author yzchao
 * @Date 2022/11/8 15:15
 * @Version V1.0
 */

public interface CartService {

    /**
     * @Description 添加购物车
     * @Date 15:19 2022/11/8
     * @Param [skuId, number, username]
     * @return void
     */
    void addCart(Long skuId,Integer number);


    /**
     * @Description 查询购物车
     * @Date 16:32 2022/11/8
     * @Param [username]
     * @return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     */
    public List<CartInfo> getCartInfo();


    /**
     * @Description 删除购物车
     * @Date 16:40 2022/11/8
     * @Param [id]
     * @return void
     */
    public void removeCart(Long id);
    
    /**
     * @Description 修改购物车中商品的数量
     * @Date 18:04 2022/11/8
     * @Param [skuId, number, username]
     * @return void
     */
    public void updateCartNum(Long id,Integer number);

    /**
     * @Description 选中状态切换
     * @Date 18:40 2022/11/8
     * @Param [id, number, username]
     * @return void
     */
    public void check(Long cartId,Short status);

    /**
     * @Description 合并用户在未登录的情况下加入的购物车的数据
     * @Date 23:31 2022/11/8
     * @Param [cartInfoList]
     * @return void
     */
    public void mergeCart(List<CartInfo> cartInfoList);

    /**
     * @return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     * @Description 查询用户本次购买的购物车数据
     * @Date 16:51 2022/11/9
     * @Param []
     */
    public Map<String, Object> getOrderConfirmCart();
    
    /**
     * @Description 清空购物车
     * @Date 23:36 2022/11/9
     * @Param []
     * @return void
     */
    public void deleteCart();
}
