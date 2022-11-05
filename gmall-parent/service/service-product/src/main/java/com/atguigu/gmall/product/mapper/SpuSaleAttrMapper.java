package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @InterfaceName SpuSaleAttrMapper.xml
 * @Description SpuSaleAttr销售属性名称表的映射
 * @Author yzchao
 * @Date 2022/10/25 15:29
 * @Version V1.0
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * @Description 根据spu的id查询这个spu的全部销售属性和每个销售属性对应的值列表
     * @Date 20:00 2022/10/25
     * @Param [spuId]
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuSaleAttr(@Param("spuId") Long spuId);
    
    /**
     * @Description 查询销售属性名称和值列表同时标识出当前选中的值是哪几个的sql实现
     * @Date 10:07 2022/10/27
     * @Param [skuId, spuId]
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuAttrBySpuIdAndSkuId(@Param("skuId")Long skuId,@Param("spuId")Long spuId);















}
