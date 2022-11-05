package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @InterfaceName BaseAttrInfoMapper
 * @Description 平台属性表的mapper映射
 * @Author yzchao
 * @Date 2022/10/22 16:32
 * @Version V1.0
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * @Description 根据分类查询平台属性列表
     * @Date 20:49 2022/10/24
     * @Param [category3Id]
     * @return java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(@Param("category3Id") Long category3Id);


    /**
     * @Description 查询指定skuId的skuInfo的平台属性 一个平台属性名称 对一个值  但是值可以有多个
     * @Date 20:49 2022/11/2
     * @Param [category3Id]
     * @return java.util.List<com.atguigu.gmall.model.product.SkuInfo>
     */
    List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@Param("skuId") Long skuId);


}
