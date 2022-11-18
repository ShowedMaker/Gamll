package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName CartForOrderController
 * @Description 内部调用 购物车微服务控制层
 * @Author yzchao
 * @Date 2022/11/9 19:26
 * @Version V1.0
 */

@RestController
@RequestMapping("api/cart")
public class CartForOrderController {

    @Autowired
    private CartService cartService;


    /**
     * @Description 下单时调用查询实时购物车和实时总金额
     * @Date 19:28 2022/11/9
     * @Param
     * @return
     */
    @GetMapping("/getOrderAddInfo")
    public Map<String,Object> getOrderAddInfo(){
         return cartService.getOrderConfirmCart();
     }


     /**
      * @Description 清空购物车数据
      * @Date 0:15 2022/11/10
      * @Param []
      * @return void
      */
     @GetMapping("/deleteCart")
     public void deleteCart(){
        cartService.deleteCart();
    }
}
