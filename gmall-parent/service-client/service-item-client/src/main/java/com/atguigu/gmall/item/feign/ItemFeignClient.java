package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @InterfaceName ItemFeignClient
 * @Description web微服务调用item微服务 feign接口
 * @Author yzchao
 * @Date 2022/10/31 19:28
 * @Version V1.0
 */
@FeignClient(value = "service-item",path = "/item")
public interface ItemFeignClient {


    @GetMapping("/getSkuInfo/{skuId}")
    public Map<String, Object> getSkuInfo(@PathVariable Long skuId);


}
