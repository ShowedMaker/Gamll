package com.atguigu.gmall.list;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @InterfaceName SearchFeignClient
 * @Description web模块微服务调用list微服务 feign接口
 * @Author yzchao
 * @Date 2022/11/4 17:50
 * @Version V1.0
 */
@FeignClient(name = "service-list",path = "api/search",contextId = "searchFeign")
public interface SearchFeignClient {

    /**
     * @Description 首页搜索数据
     * @Date 17:53 2022/11/4
     * @Param [searchData]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping("/searchGoods")
    public Map<String, Object> searchGoods(@RequestParam Map<String,Object> searchData);


}
