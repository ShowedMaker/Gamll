package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * @InterfaceName SkuInfoMapper
 * @Description SpuInfo表的mapper映射
 * @Author yzchao
 * @Date 2022/10/25 21:17
 * @Version V1.0
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * @Description 扣减库存
     * @Date 18:21 2022/11/13
     * @Param [stock, skuId]
     * @return int
     */
    @Update("update sku_info set stock = stock - #{stock} where stock >= ${stock} and id = #{skuId}")
    public int decountStock(@Param("stock") Integer stock, @Param("skuId") Integer skuId);

    /**
     * @Description 回滚库存
     * @Date 18:25 2022/11/13
     * @Param [stock, skuId]
     * @return int
     */
    @Update("update sku_info set stock = stock + ${stock} where id = #{skuId}")
    public int rollBackStock(@Param("stock") Integer stock,@Param("skuId") Integer skuId);

    
}
