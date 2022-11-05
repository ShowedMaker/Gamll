package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @InterfaceName IndexService
 * @Description 首页信息查询的分类信息的接口
 * @Author yzchao
 * @Date 2022/11/1 15:32
 * @Version V1.0
 */

public interface IndexService {

    /**
     * 首页信息查询的分类信息的接口
     *
     * @return void
     * @Description
     * @Date 15:47 2022/11/1
     * @Param []
     */
    public List<JSONObject> getIndexCategory();

}
