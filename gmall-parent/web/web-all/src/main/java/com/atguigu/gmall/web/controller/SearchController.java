package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.list.SearchFeignClient;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName SearchController
 * @Description 首页商品搜索的controller
 * @Author yzchao
 * @Date 2022/11/4 16:36
 * @Version V1.0
 */
@Controller
@RequestMapping("/api/search")
public class SearchController {

    //商品详情的url
    @Value("${item.url}")
    private String itemUrl;

    @Autowired
    private SearchFeignClient searchFeignClient;


    @GetMapping
    public String list(@RequestParam Map<String,Object> searchData, Model model){
        //由于spring的RequestParam注解接收的参数是来自于requestHeader中，即请求头，也就是在url中，格式为xxx?username=123&password=456
        Map<String, Object> result = searchFeignClient.searchGoods(searchData);
        //传给前端
        model.addAllAttributes(result);
        model.addAttribute("searchData",searchData);
        String url = getUrl(searchData);
        String sortUrl = getSortUrl(searchData);
        model.addAttribute("url",url);
        model.addAttribute("sortUrl",sortUrl);

        //获取真实的页码
        Integer pageNum = getPageNum((String) searchData.get("pageNum"));
        //符合条件的总数居
        long totalHits = Long.parseLong(result.get("totalHits").toString());

        //每页显示条数
        int size = 50;

        //分页对象初始化
        Page<Object> pageInfo = new Page<>(totalHits, pageNum, size);
        model.addAttribute("pageInfo",pageInfo);

        model.addAttribute("itemUrl",itemUrl);
        //返回页面
        return "list";
    }

    //获取页面请求最新的url
    private String getUrl(Map<String, Object> searchData) {

        StringBuffer sb = new StringBuffer("/api/search?");

        searchData.entrySet().stream().forEach(entry->{
            String key = entry.getKey();

    //排序保留 分页不保留
            if(!key.equals("pageNum")){

                String value = (String) entry.getValue();
                sb.append(key).append("=").append(value).append("&");
            }
        });

        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    //排序的url
    private String getSortUrl(Map<String, Object> searchData) {

        StringBuffer sb = new StringBuffer("/api/search?");

        searchData.entrySet().stream().forEach(entry->{
            String key = entry.getKey();
            //排序 也不保留分页
            if(!key.equals("sortFiled") && !key.equals("sortRule") && !key.equals("pageNum")){
                String value = (String) entry.getValue();
                sb.append(key).append("=").append(value).append("&");
            }
        });

        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    //获取分页的当前页码
    private Integer getPageNum(String pageNum) {

        try {
            //可以发生异常 如果用户输入非数字页码
            int i = Integer.parseInt(pageNum);
            if( i <= 0 || i > 200){
                return 1;
            }else{
                return i;
            }
        } catch (NumberFormatException e) {
            //发生异常返回默认页面 1页
            return 1;
        }


    }
}
