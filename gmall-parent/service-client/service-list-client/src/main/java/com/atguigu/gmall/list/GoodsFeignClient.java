package com.atguigu.gmall.list;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @InterfaceName GoodsFeignClient
 * @Description list微服务调用product微服务 feign接口
 * @Author yzchao
 * @Date 2022/11/2 19:24
 * @Version V1.0
 */
@FeignClient(name = "service-list",path = "api/list",contextId = "goodsFeign")
public interface GoodsFeignClient {


    @GetMapping("/goodsFromDBToEs/{skuId}")
    public Result goodsFromDBToEs(@PathVariable(value = "skuId") Long skuId);


    @GetMapping("/goodsRemoveFromEs/{skuId}")
    public Result goodsRemoveFromEs(@PathVariable(value = "skuId") Long skuId);

}
