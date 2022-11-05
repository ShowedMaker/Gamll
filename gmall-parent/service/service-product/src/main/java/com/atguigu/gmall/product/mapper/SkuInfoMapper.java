package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @InterfaceName SkuInfoMapper
 * @Description SpuInfo表的mapper映射
 * @Author yzchao
 * @Date 2022/10/25 21:17
 * @Version V1.0
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
}
