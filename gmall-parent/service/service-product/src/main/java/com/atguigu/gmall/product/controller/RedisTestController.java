package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.RedisTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RedisTestController
 * @Description
 * @Author yzchao
 * @Date 2022/10/29 16:14
 * @Version V1.0
 */
@RestController
@RequestMapping("test/product")
public class RedisTestController {

    @Autowired
    private RedisTestService redisTestService;

    @GetMapping("/setRedis")
    public Result setRedis(){
        redisTestService.setRedisByRedisson();
        return Result.ok();
    }
}
