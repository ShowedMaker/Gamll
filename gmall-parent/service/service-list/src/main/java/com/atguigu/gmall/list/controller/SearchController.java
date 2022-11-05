package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.list.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName SearchController
 * @Description 商品首页搜索控制层
 * @Author yzchao
 * @Date 2022/11/3 15:03
 * @Version V1.0
 */
@RequestMapping("/api/search")
@RestController
public class SearchController {

    @Resource
    private SearchService searchService;

    @GetMapping("/searchGoods")
    public Map<String, Object> searchGoods(@RequestParam Map<String,String> searchData){
        return searchService.searchGoods(searchData);
    }

}

