package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.util.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName SeckillGoodsController
 * @Description 秒杀商品的控制层
 * @Author yzchao
 * @Date 2022/11/14 19:11
 * @Version V1.0
 */
@RestController
@RequestMapping("/seckill/goods")
public class SeckillGoodsController {
    
    @Resource
    private SeckillGoodsService seckillGoodsService;
    
    /**
     * @Description 给客户调的 前端查看秒杀商品时间段
     * @Date 19:22 2022/11/14
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping
    public Result getSeckillTimeMenu(){
        return Result.ok(DateUtil.getDateMenus());
    }
    
    /**
     * @Description 查询指定时间段的redis中的秒杀商品
     * @Date 19:40 2022/11/14
     * @Param [time]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getSeckillGoodsList")
    public Result getSeckillGoodsList(String time){
        return Result.ok(seckillGoodsService.getSeckillGoodsList(time));
    }


    /**
     * @Description 查询指定时间段的指定goodsId的redis中的秒杀商品
     * @Date 19:52 2022/11/14
     * @Param [time, goodsId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getSeckillGoods")
    public Result getSeckillGoods(String time,String goodsId){
        return Result.ok(seckillGoodsService.getSeckillGoods(time,goodsId));
    }
}
