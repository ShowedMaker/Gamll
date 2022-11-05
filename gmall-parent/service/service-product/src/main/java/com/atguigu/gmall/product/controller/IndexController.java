package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.Java0500GmallCache;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName IndexController
 * @Description 首页信息查询的分类信息的控制层
 * @Author yzchao
 * @Date 2022/11/1 16:58
 * @Version V1.0
 */
@RequestMapping("api/index")
@RestController
public class IndexController {

    @Resource
    private IndexService indexService;

    @GetMapping("/getIndexCategory")
    @Java0500GmallCache
    public List<JSONObject> getIndexCategory(){
        return indexService.getIndexCategory();
    }


}
