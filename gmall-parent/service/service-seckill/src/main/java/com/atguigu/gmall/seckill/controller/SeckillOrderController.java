package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.pojo.UserRecode;
import com.atguigu.gmall.seckill.service.SeckillOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName SeckillOrderController
 * @Description 秒杀订单的控制层
 * @Author yzchao
 * @Date 2022/11/14 20:23
 * @Version V1.0
 */
@RestController
@RequestMapping("/seckill/order")
public class SeckillOrderController {

    @Resource
    private SeckillOrderService seckillOrderService;


    /**
     * @Description 返回用户排队状态
     * @Date 20:26 2022/11/14
     * @Param [userRecode]
     * @return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/addSeckillOrder")
    public Result addSeckillOrder(@RequestBody UserRecode userRecode){
       return Result.ok(seckillOrderService.addSeckillOrder(userRecode));
    }


    /**
     * @Description 根据用户名查询 用户排队状态userRecode
     * @Date 21:03 2022/11/14
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getUserRecode")
    public Result getUserRecode(){
        return Result.ok(seckillOrderService.getUserRecode());
    }


    /**
     * @Description 主动取消订单
     * @Date 18:59 2022/11/16
     * @Param [username]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/cancelSeckillOrder")
    public Result cancelSeckillOrder(String username){
        seckillOrderService.cancelSeckillOrder(null);
        return Result.ok();
    }



}
