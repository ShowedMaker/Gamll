package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @InterfaceName BaseAttrInfoService
 * @Description 平台属性相关的接口类
 * @Author yzchao
 * @Date 2022/10/22 16:12
 * @Version V1.0
 */

public interface BaseAttrInfoService {

    /**
     * @Description 主键查询
     * @return: com.atguigu.gmall.model.product.BaseAttrInfo
     * @Date 2022/10/22 16:55
     */
    BaseAttrInfo getBaseAttrInfo(Long id);

    /**
     * @Description 查询全部
     * @return: java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/22 16:56
     */
    List<BaseAttrInfo> getAll();

    /**
     * @Description 新增
     * @Date 2022/10/22 16:57
     */
    void add(BaseAttrInfo baseAttrInfo);

    /**
     * @Description 更新
     * @param: baseAttrInfo
     * @Date 2022/10/22 21:43
     */
    void update(BaseAttrInfo baseAttrInfo);

    /**
     * @Description 删除
     * @Date 2022/10/22 21:44
     */
    void delete(Long id);


  /**
   * @Description 条件查询
   * @param: baseAttrInfo
   * @return: java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
   * @Date 2022/10/22 23:59
   */
    List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo);


    /**
     * @Description 分页查询
     * @param: page
     * @param: size
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/23 0:00
     */
    IPage<BaseAttrInfo> search(Long page,Long size);


    /**
     * @Description 分页条件查询
     * @param: page
     * @param: size
     * @param: baseAttrInfo
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/23 0:01
     */
    IPage<BaseAttrInfo> search(Long page,Long size,BaseAttrInfo baseAttrInfo);


}
