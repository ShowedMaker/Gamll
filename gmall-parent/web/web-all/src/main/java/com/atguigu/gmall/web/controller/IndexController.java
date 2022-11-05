package com.atguigu.gmall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.feign.IndexFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName IndexController
 * @Description 打开前端首页控制层
 * @Author yzchao
 * @Date 2022/11/1 18:11
 * @Version V1.0
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @Resource
    private IndexFeignClient indexFeignClient;

    @GetMapping
    public String index(Model model){
        List<JSONObject> categoryList =
                indexFeignClient.getIndexCategory();
        model.addAttribute("categoryList",categoryList);
        return "index";
    }


}
