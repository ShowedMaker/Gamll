package com.atguigu.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @InterfaceName IndexFeignClient
 * @Description web微服务调用product微服务 feign接口
 * @Author yzchao
 * @Date 2022/11/1 18:08
 * @Version V1.0
 */
@FeignClient(value = "service-product", path = "/api/index",contextId="IndexFeign")
public interface IndexFeignClient {


    @GetMapping("/getIndexCategory")
    public List<JSONObject> getIndexCategory();


}
