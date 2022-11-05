package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName ItemController
 * @Description
 * @Author yzchao
 * @Date 2022/10/26 18:43
 * @Version V1.0
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    @GetMapping("/getSkuInfo/{skuId}")
    public Map<String, Object> getSkuInfo(@PathVariable Long skuId){
       return itemService.getSkuInfo(skuId);

    }


}
