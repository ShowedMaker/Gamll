package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @InterfaceName CartFeignClient
 * @Description order微服务调用cart微服务 feign接口
 * @Author yzchao
 * @Date 2022/10/26 19:13
 * @Version V1.0
 */

@FeignClient(value = "service-cart", path = "/api/cart",contextId = "CartFeign")
public interface CartFeignClient {

    /**
     * @Description 下单时调用查询实时购物车和实时总金额
     * @Date 19:28 2022/11/9
     * @Param
     * @return
     */
    @GetMapping("/getOrderAddInfo")
    public Map<String,Object> getOrderAddInfo();

    /**
     * @Description 清空购物车数据
     * @Date 0:18 2022/11/10
     * @Param []
     * @return void
     */
    @GetMapping("/deleteCart")
    public void deleteCart();

}
