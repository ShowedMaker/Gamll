package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @InterfaceName SpuSaleAttrValue
 * @Description SkuSaleAttrValue表映射
 * @Author yzchao
 * @Date 2022/10/25 15:30
 * @Version V1.0
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * @Description 切换销售属性值选中时的值
     * @Date 11:11 2022/10/27
     * @Param [spuId]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    @Select("SELECT sku_id,GROUP_CONCAT( DISTINCT sale_attr_value_id ORDER BY sale_attr_value_id SEPARATOR ',' ) " +
            "AS values_id FROM sku_sale_attr_value WHERE spu_id = #{spuId} GROUP BY sku_id")
    List<Map<Object,Object>> getSkuValuesList(@Param("spuId") Long spuId);
}
