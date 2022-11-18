package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName CartController
 * @Description 购物车控制层
 * @Author yzchao
 * @Date 2022/11/8 15:37
 * @Version V1.0
 */
@RestController
@RequestMapping("api/cart")
public class CartController {


    @Resource
    private CartService cartService;

    /**
     * @Description 添加购物车
     * @Date 16:32 2022/11/8
     * @Param [skuId, number]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/addCart")
    public Result addCart(Long skuId, Integer number){

        cartService.addCart(skuId,number);
        return Result.ok();
    }

    /**
     * @Description 查看购物车
     * @Date 16:33 2022/11/8
     * @Param [username]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getCartInfo")
    public Result getCartInfo(){
        return Result.ok(cartService.getCartInfo());
    }
    
    /**
     * @Description 删除购物车
     * @Date 16:49 2022/11/8
     * @Param [id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/removeCart")
    public Result removeCart(Long id){
        cartService.removeCart(id);
        return Result.ok();
    }


    /**
     * @Description 修改购物车中商品数量
     * @Date 18:40 2022/11/8
     * @Param [id, number]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/updateCartNum")
    public Result updateCartNum(Long id,Integer number){
        cartService.updateCartNum(id,number);
        return Result.ok();
    }

    /**
     * @Description 更改选中状态
     * @Date 19:05 2022/11/8
     * @Param [status, cartId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/check")
    public Result check(Long cartId){
        cartService.check(cartId, (short) 1);
        return Result.ok();
    }


    @GetMapping("/uncheck")
    public Result unCheck(Long cartId){
        cartService.check(cartId, (short) 0);
        return Result.ok();
    }
    
    /**
     * @Description 合并用户在未登录的情况下加入的购物车的数据
     * @Date 23:30 2022/11/8
     * @Param [cartInfoList]
     * @return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/mergeCart")
    public Result mergeCart(@RequestBody List<CartInfo> cartInfoList){
        cartService.mergeCart(cartInfoList);
        return Result.ok();
    }


    /**
     * @Description 获取订单确认页面相关的购物车信息
     * @Date 19:24 2022/11/9
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getConfirmCart")
    public Result getConfirmCart(){
        return Result.ok(cartService.getOrderConfirmCart());
    }
}

