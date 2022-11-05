package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.item.feign.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @ClassName ItemController
 * @Description 前端商品详情页面控制层
 * @Author yzchao
 * @Date 2022/10/31 19:19
 * @Version V1.0
 */
@Controller
@RequestMapping("/item")
public class ItemController {

    @Resource
    private ItemFeignClient itemFeignClient;

    /**
     * @Description 打开商品详情页面
     * @Date 22:28 2022/10/31
     * @Param [skuId, model]
     * @return java.lang.String
     */
    @GetMapping("/{skuId}")
    public String getItemPage(@PathVariable Long skuId, Model model){
        Map<String, Object> map = itemFeignClient.getSkuInfo(skuId);
        if (map == null || map.isEmpty()) {
            return "404";
        }
        model.addAllAttributes(map);
        return "item";
    }


    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/createHtml/{skuId}")
    @ResponseBody
    public String createHtml(@PathVariable Long skuId) throws FileNotFoundException, UnsupportedEncodingException {
        //查询指定的商品页面需要的全部数据
        Map<String, Object> skuInfo = itemFeignClient.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.isEmpty()) {
            return "商品不存在啊";
        }

        //初始化数据容器对象Model
        Context context = new Context();
        context.setVariables(skuInfo);
        //初始化文件输出对象
        File file = new File("d:/",skuId+".html");
        PrintWriter ps = new PrintWriter(file,"utf-8");

        //生成静态页面到指定的目录去

       /*
        需要的三个参数
        1.模板页面是哪个
        2.数据装在哪里
        3.生成好的页面保存到哪里
        */
        templateEngine.process("item",context,ps);
        ps.flush();
        ps.close();

        return "创建页面成功了！";
    }


}
