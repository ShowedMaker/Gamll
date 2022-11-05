package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * @InterfaceName SearchService
 * @Description 商品搜索的接口
 * @Author yzchao
 * @Date 2022/11/3 14:09
 * @Version V1.0
 */

public interface SearchService {

    /**
     * @return java.util.List<com.atguigu.gmall.model.list.Goods>
     * @Description 首页商品商品输入框搜索
     * @Date 14:11 2022/11/3
     * @Param [search]
     */
    Map<String, Object> searchGoods(Map<String,String> search);
}
