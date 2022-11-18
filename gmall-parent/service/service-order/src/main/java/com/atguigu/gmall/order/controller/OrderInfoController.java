package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName OrderInfoController
 * @Description 订单服务的控制层
 * @Author yzchao
 * @Date 2022/11/9 19:12
 * @Version V1.0
 */
@RestController
@RequestMapping("/api/order")
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Description 用户新增订单
     * @Date 16:39 2022/11/13
     * @Param [orderInfo]
     * @return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo){
        orderInfoService.addOrder(orderInfo);
        return Result.ok();
    }

    /**
     * @Description 给用户调的 主动取消订单
     * @Date 16:34 2022/11/13
     * @Param [orderId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/cancelOrder")
    public Result cancelOrder(Long orderId){
        orderInfoService.cancelOrder(orderId);
        return Result.ok();
    }
}
